package com.github.hotm.world.meta.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.meta.MetaAccess
import com.github.hotm.meta.MetaBlock
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import org.apache.logging.log4j.LogManager
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.math.abs

class ClientMetaStorage(override val world: ClientWorld, private val radius: Int) : MetaAccess {
    companion object {
        private val LOGGER = LogManager.getLogger()

        @JvmStatic
        fun getChunkMapRadius(loadDistance: Int): Int {
            return loadDistance.coerceAtLeast(2) + 3
        }

        private fun positionEquals(chunk: ClientMetaChunkPillar?, x: Int, y: Int): Boolean {
            return chunk?.let {
                val chunkPos = it.pos
                chunkPos.x == x && chunkPos.z == y
            } ?: false
        }
    }

    private val diameter = radius * 2 + 1
    private val chunks = AtomicReferenceArray<ClientMetaChunkPillar?>(diameter * diameter)

    private var loadedChunkCount = 0

    @Volatile
    private var centerChunkX = 0

    @Volatile
    private var centerChunkZ = 0

    override val isClient = true

    override fun getUpdateListener(pos: ChunkSectionPos): Runnable? {
        return null
    }

    override fun getBaseAura(pos: ChunkSectionPos): Float {
        return getChunk(pos)?.baseAura?.invoke() ?: 0f
    }

    override fun get(pos: BlockPos): MetaBlock? {
        val sectionPos = ChunkSectionPos.from(pos)
        return getChunk(sectionPos)?.getNode(pos)
    }

    override fun getAllBy(pos: ChunkSectionPos, filter: Predicate<MetaBlock>): Stream<MetaBlock> {
        return getChunk(pos)?.getAllBy(filter) ?: Stream.empty()
    }

    override fun recalculateSiphons(pos: ChunkSectionPos, visitedNodes: MutableSet<DimBlockPos>) {
        // Nothing to do client-side
    }

    fun receiveChunkPillar(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        if (isInRadius(chunkX, chunkZ)) {
            val pos = ChunkPos(chunkX, chunkZ)
            val newPillar = ClientMetaChunkPillar.fromPacket(this, pos, buf, ctx)
            set(chunkX, chunkZ, newPillar)
        } else {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ)
            ctx.drop()
        }
    }

    fun receivePut(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val pos = buf.readBlockPos()
        val sectionPos = ChunkSectionPos.from(pos)
        getChunk(sectionPos)?.receivePut(this, pos, buf, ctx)
    }

    fun receiveRemove(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val pos = buf.readBlockPos()
        val sectionPos = ChunkSectionPos.from(pos)
        getChunk(sectionPos)?.receiveRemove(pos)
    }

    fun receiveBaseAuraUpdate(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val sectionPos = ChunkSectionPos.from(buf.readVarInt(), buf.readVarInt(), buf.readVarInt())
        getChunk(sectionPos)?.let { chunk ->
            val baseAura = buf.readFloat()
            chunk.baseAura = { baseAura }
        }
    }

    fun updateCenterChunk(centerChunkX: Int, centerChunkZ: Int) {
        this.centerChunkX = centerChunkX
        this.centerChunkZ = centerChunkZ
    }

    fun withLoadDistance(radius: Int): ClientMetaStorage {
        if (radius == this.radius) return this

        val newStorage = ClientMetaStorage(world, radius)
        newStorage.centerChunkX = centerChunkX
        newStorage.centerChunkZ = centerChunkZ

        for (i in 0 until chunks.length()) {
            chunks.get(i)?.let { chunk ->
                val pos = chunk.pos
                if (newStorage.isInRadius(pos.x, pos.z)) {
                    newStorage.set(pos.x, pos.z, chunk)
                }
            }
        }

        return newStorage
    }

    private fun isInRadius(chunkX: Int, chunkZ: Int): Boolean {
        return abs(chunkX - centerChunkX) <= radius && abs(chunkZ - centerChunkZ) <= radius
    }

    private fun getIndex(chunkX: Int, chunkZ: Int): Int {
        return Math.floorMod(chunkZ, diameter) * diameter + Math.floorMod(chunkX, diameter)
    }

    private fun set(chunkX: Int, chunkZ: Int, chunk: ClientMetaChunkPillar?) {
        val index = getIndex(chunkX, chunkZ)
        val curChunk = chunks.getAndSet(index, chunk)
        if (curChunk != null) {
            loadedChunkCount--
            // perform unloading actions here
        }

        if (chunk != null) {
            loadedChunkCount++
        }
    }

    private fun compareAndSet(
        index: Int, expected: ClientMetaChunkPillar, new: ClientMetaChunkPillar?
    ): ClientMetaChunkPillar {
        if (chunks.compareAndSet(index, expected, new) && new == null) {
            loadedChunkCount--
        }

        // perform unloading actions here
        return expected
    }

    fun unload(chunkX: Int, chunkZ: Int) {
        if (isInRadius(chunkX, chunkZ)) {
            val index = getIndex(chunkX, chunkZ)
            chunks.get(index)?.let { curChunk ->
                if (positionEquals(curChunk, chunkX, chunkZ)) {
                    compareAndSet(index, curChunk, null)
                }
            }
        }
    }

    private fun getChunkPillar(chunkX: Int, chunkZ: Int): ClientMetaChunkPillar? {
        if (isInRadius(chunkX, chunkZ)) {
            val chunk = chunks.get(getIndex(chunkX, chunkZ))
            if (positionEquals(chunk, chunkX, chunkZ)) {
                return chunk
            }
        }

        return null
    }

    private fun getChunk(pos: ChunkSectionPos): ClientMetaChunk? {
        getChunkPillar(pos.x, pos.z)?.let { pillar ->
            if (pos.y < world.bottomSectionCoord || pos.y > world.topSectionCoord) return null

            val index = pos.y - world.bottomSectionCoord
            return pillar.chunks[index]
        }

        return null
    }
}