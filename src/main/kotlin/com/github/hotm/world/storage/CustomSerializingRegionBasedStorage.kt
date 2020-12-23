package com.github.hotm.world.storage

import com.github.hotm.HotMConstants
import com.github.hotm.mixinapi.StorageUtils
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.mojang.datafixers.DataFixer
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Dynamic
import com.mojang.serialization.DynamicOps
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.util.Util
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import net.minecraft.world.storage.StorageIoWorker
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.BooleanSupplier

abstract class CustomSerializingRegionBasedStorage<R : Any>(
    directory: File,
    private val codecFactory: (Runnable) -> Codec<R>,
    private val factory: (Runnable) -> R,
    private val dataFixer: DataFixer,
    private val dataFixType: DataFixTypes?,
    bl: Boolean
) : AutoCloseable {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private var worker: StorageIoWorker = StorageUtils.newStorageIoWorker(directory, bl, directory.name)
    private val loadedElements: Long2ObjectMap<Optional<R>?> = Long2ObjectOpenHashMap()
    private val unsavedElements = LongLinkedOpenHashSet()

    fun tick(shouldKeepTicking: BooleanSupplier) {
        while (!unsavedElements.isEmpty() && shouldKeepTicking.asBoolean) {
            val chunkPos = ChunkSectionPos.from(unsavedElements.firstLong()).toChunkPos()
            save(chunkPos)
        }
    }

    protected fun getIfLoaded(pos: Long): Optional<R>? {
        return loadedElements[pos]
    }

    protected operator fun get(pos: Long): Optional<R> {
        val chunkSectionPos = ChunkSectionPos.from(pos)
        return if (isPosInvalid(chunkSectionPos)) {
            Optional.empty()
        } else {
            var optional = getIfLoaded(pos)
            if (optional != null) {
                optional
            } else {
                loadDataAt(chunkSectionPos.toChunkPos())
                optional = getIfLoaded(pos)
                optional
                    ?: throw (Util.throwOrPause(IllegalStateException()) as IllegalStateException)
            }
        }
    }

    protected fun isPosInvalid(pos: ChunkSectionPos): Boolean {
        return World.isHeightInvalid(ChunkSectionPos.getBlockCoord(pos.sectionY))
    }

    protected fun getOrCreate(pos: Long): R {
        val optional = this[pos]
        return if (optional.isPresent) {
            optional.get()
        } else {
            val newObj = factory(Runnable { onUpdate(pos) })
            loadedElements[pos] = Optional.of(newObj)
            newObj
        }
    }

    private fun loadDataAt(chunkPos: ChunkPos) {
        update(chunkPos, NbtOps.INSTANCE, loadNbt(chunkPos))
    }

    private fun loadNbt(pos: ChunkPos): CompoundTag? {
        return try {
            worker.getNbt(pos)
        } catch (var3: IOException) {
            LOGGER.error("Error reading chunk {} data from disk", pos, var3)
            null
        }
    }

    private fun <T> update(pos: ChunkPos, dynamicOps: DynamicOps<T>, data: T?) {
        if (data == null) {
            for (i in 0..15) {
                loadedElements[ChunkSectionPos.from(pos, i).asLong()] = Optional.empty()
            }
        } else {
            val dynamic: Dynamic<T> = Dynamic(dynamicOps, data)
            val savedVersion = getDataVersion(dynamic)
            val savedNeedsUpdate = dataFixType != null && savedVersion != HotMConstants.DATA_VERSION
            val dataFixed: Dynamic<T> = dataFixType?.let {
                dataFixer.update(it.typeReference, dynamic, savedVersion, HotMConstants.DATA_VERSION)
            } ?: dynamic
            val optionalDynamic = dataFixed["Sections"]

            for (l in 0..15) {
                val m = ChunkSectionPos.from(pos, l).asLong()
                val optional = optionalDynamic[l.toString()].result().flatMap { dynamicx: Dynamic<T> ->
                    (codecFactory(Runnable { onUpdate(m) })).parse(dynamicx).resultOrPartial(LOGGER::error)
                }
                loadedElements[m] = optional
                optional.ifPresent {
                    onLoad(m)
                    if (savedNeedsUpdate) {
                        onUpdate(m)
                    }
                }
            }
        }
    }

    private fun save(chunkPos: ChunkPos) {
        val dynamic = method_20367(chunkPos, NbtOps.INSTANCE)
        val tag = dynamic.value
        if (tag is CompoundTag) {
            worker.setResult(chunkPos, tag)
        } else {
            LOGGER.error("Expected compound tag, got {}", tag)
        }
    }

    private fun <T> method_20367(chunkPos: ChunkPos, dynamicOps: DynamicOps<T>): Dynamic<T> {
        val map: MutableMap<T, T?> = Maps.newHashMap()
        for (i in 0..15) {
            val l = ChunkSectionPos.from(chunkPos, i).asLong()
            unsavedElements.remove(l)
            val optional: Optional<R>? = loadedElements[l]
            if (optional != null && optional.isPresent) {
                val dataResult: DataResult<T> =
                    (codecFactory(Runnable { onUpdate(l) })).encodeStart(dynamicOps, optional.get())
                val string = i.toString()
                dataResult.resultOrPartial(LOGGER::error)
                    .ifPresent { obj: T -> map[dynamicOps.createString(string)] = obj }
            }
        }
        return Dynamic(
            dynamicOps,
            dynamicOps.createMap(
                ImmutableMap.of(
                    dynamicOps.createString("Sections"),
                    dynamicOps.createMap(map),
                    dynamicOps.createString("DataVersion"),
                    dynamicOps.createInt(HotMConstants.DATA_VERSION)
                )
            )
        )
    }

    protected fun onLoad(pos: Long) {}

    protected fun onUpdate(pos: Long) {
        val optional: Optional<R>? = loadedElements[pos]
        if (optional != null && optional.isPresent) {
            unsavedElements.add(pos)
        } else {
            LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos))
        }
    }

    private fun getDataVersion(dynamic: Dynamic<*>): Int {
        return dynamic["DataVersion"].asInt(99)
    }

    fun method_20436(chunkPos: ChunkPos) {
        if (!unsavedElements.isEmpty()) {
            for (i in 0..15) {
                val l = ChunkSectionPos.from(chunkPos, i).asLong()
                if (unsavedElements.contains(l)) {
                    save(chunkPos)
                    return
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun close() {
        worker.close()
    }
}