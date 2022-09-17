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
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.util.Util
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.HeightLimitView
import net.minecraft.world.storage.StorageIoWorker
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.BooleanSupplier

abstract class CustomSerializingRegionBasedStorage<R : Any>(
    directory: File,
    private val dataFixer: DataFixer,
    private val dataFixType: DataFixTypes?,
    dsync: Boolean,
    private val world: HeightLimitView
) : AutoCloseable {
    companion object {
        private val LOGGER = LogManager.getLogger()

        private fun asLong(pos: ChunkPos, y: Int): Long = ChunkSectionPos.asLong(pos.x, y, pos.z)
    }

    private var worker: StorageIoWorker = StorageUtils.newStorageIoWorker(directory, dsync, directory.name)
    private val loadedElements: Long2ObjectMap<Optional<R>?> = Long2ObjectOpenHashMap()
    private val unsavedElements = LongLinkedOpenHashSet()

    protected abstract fun factory(updateListener: Runnable): R
    protected abstract fun codecFactory(updateListener: Runnable): Codec<R>

    open fun tick(shouldKeepTicking: BooleanSupplier) {
        while (!unsavedElements.isEmpty() && shouldKeepTicking.asBoolean) {
            val chunkPos = ChunkSectionPos.from(unsavedElements.firstLong()).toChunkPos()
            save(chunkPos)
        }
    }

    protected fun getIfLoaded(pos: Long): Optional<R>? {
        return loadedElements[pos]
    }

    protected operator fun get(pos: Long): Optional<R> {
        return if (isPosInvalid(pos)) {
            Optional.empty()
        } else {
            var optional = getIfLoaded(pos)
            if (optional != null) {
                optional
            } else {
                loadDataAt(ChunkSectionPos.from(pos).toChunkPos())
                optional = getIfLoaded(pos)
                optional ?: throw Util.throwOrPause(IllegalStateException("Error loading chunk section"))
            }
        }
    }

    protected fun isPosInvalid(pos: Long): Boolean {
        return world.isOutOfHeightLimit(ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(pos)))
    }

    protected fun getOrCreate(pos: Long): R {
        if (isPosInvalid(pos)) {
            throw Util.throwOrPause(IllegalArgumentException("sectionPos out of bounds"))
        } else {
            val optional = this[pos]
            return if (optional.isPresent) {
                optional.get()
            } else {
                val newObj = factory { onUpdate(pos) }
                loadedElements[pos] = Optional.of(newObj)
                newObj
            }
        }
    }

    private fun loadDataAt(chunkPos: ChunkPos) {
        update(chunkPos, NbtOps.INSTANCE, loadNbt(chunkPos).join().orElse(null))
    }

    private fun loadNbt(pos: ChunkPos): CompletableFuture<Optional<NbtCompound>> {
        return worker.readChunkData(pos).exceptionally {
            if (it is IOException) {
                LOGGER.warn("Error reading chunk {} data from disk", pos, it)
                Optional.empty()
            } else {
                throw CompletionException(it)
            }
        }
    }

    private fun <T> update(pos: ChunkPos, dynamicOps: DynamicOps<T>, data: T?) {
        if (data == null) {
            for (i in world.bottomSectionCoord until world.topSectionCoord) {
                loadedElements[asLong(pos, i)] = Optional.empty()
            }
        } else {
            val dynamic: Dynamic<T> = Dynamic(dynamicOps, data)
            val savedVersion = getDataVersion(dynamic)
            val savedNeedsUpdate = dataFixType != null && savedVersion != HotMConstants.DATA_VERSION
            val dataFixed: Dynamic<T> = dataFixType?.let {
                dataFixer.update(it.typeReference, dynamic, savedVersion, HotMConstants.DATA_VERSION)
            } ?: dynamic
            val optionalDynamic = dataFixed["Sections"]

            for (l in world.bottomSectionCoord until world.topSectionCoord) {
                val m = asLong(pos, l)
                val optional = optionalDynamic[l.toString()].result().flatMap { dynamicx: Dynamic<T> ->
                    (codecFactory { onUpdate(m) }).parse(dynamicx).resultOrPartial(LOGGER::error)
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
        val dynamic = writeDynamic(chunkPos, NbtOps.INSTANCE)
        val tag = dynamic.value
        if (tag is NbtCompound) {
            worker.setResult(chunkPos, tag)
        } else {
            LOGGER.error("Expected compound tag, got {}", tag)
        }
    }

    private fun <T> writeDynamic(chunkPos: ChunkPos, dynamicOps: DynamicOps<T>): Dynamic<T> {
        val map: MutableMap<T, T> = Maps.newHashMap()
        for (i in world.bottomSectionCoord until world.topSectionCoord) {
            val l = asLong(chunkPos, i)
            unsavedElements.remove(l)
            val optional: Optional<R>? = loadedElements[l]
            if (optional != null && optional.isPresent) {
                val dataResult: DataResult<T> =
                    (codecFactory { onUpdate(l) }).encodeStart(dynamicOps, optional.get())
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

    protected open fun onLoad(pos: Long) {}

    protected open fun onUpdate(pos: Long) {
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

    fun trySave(chunkPos: ChunkPos) {
        if (!unsavedElements.isEmpty()) {
            for (i in world.bottomSectionCoord until world.topSectionCoord) {
                val l = asLong(chunkPos, i)
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
