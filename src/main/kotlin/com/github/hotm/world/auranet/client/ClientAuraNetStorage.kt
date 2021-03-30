package com.github.hotm.world.auranet.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.world.auranet.AuraNetAccess
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import org.apache.logging.log4j.LogManager
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.math.abs

class ClientAuraNetStorage(override val world: ClientWorld, private val radius: Int) : AuraNetAccess {
    companion object {
        private val LOGGER = LogManager.getLogger()

        @JvmStatic
        fun getChunkMapRadius(loadDistance: Int): Int {
            return loadDistance.coerceAtLeast(2) + 3
        }

        private fun positionEquals(chunk: ClientAuraNetChunkPillar?, x: Int, y: Int): Boolean {
            return chunk?.let {
                val chunkPos = it.pos
                chunkPos.x == x && chunkPos.z == y
            } ?: false
        }
    }

    private val diameter = radius * 2 + 1
    private val chunks = AtomicReferenceArray<ClientAuraNetChunkPillar?>(diameter * diameter)

    private var loadedChunkCount = 0

    @Volatile
    private var centerChunkX = 0

    @Volatile
    private var centerChunkZ = 0

    override val isClient = true

    override fun getBaseAura(pos: ChunkSectionPos): Int {
        return getChunk(pos)?.baseAura ?: 0
    }

    override fun get(pos: BlockPos): AuraNode? {
        val sectionPos = ChunkSectionPos.from(pos)
        return getChunk(sectionPos)?.getNode(pos)
    }

    override fun getAllBy(pos: ChunkSectionPos, filter: Predicate<AuraNode>): Stream<AuraNode> {
        return getChunk(pos)?.getAllBy(filter) ?: Stream.empty()
    }

    fun receiveChunkPillar(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        if (isInRadius(chunkX, chunkZ)) {
            val pos = ChunkPos(chunkX, chunkZ)
            val newPillar = ClientAuraNetChunkPillar.fromPacket(this, pos, buf, ctx)
            set(chunkX, chunkZ, newPillar)
        } else {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ)
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
            val baseAura = buf.readVarUnsignedInt()
            chunk.baseAura = baseAura
        }
    }

    fun updateCenterChunk(centerChunkX: Int, centerChunkZ: Int) {
        this.centerChunkX = centerChunkX
        this.centerChunkZ = centerChunkZ
    }

    fun withLoadDistance(radius: Int): ClientAuraNetStorage {
        if (radius == this.radius) return this

        val newStorage = ClientAuraNetStorage(world, radius)
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

    private fun set(chunkX: Int, chunkZ: Int, chunk: ClientAuraNetChunkPillar?) {
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
        index: Int, expected: ClientAuraNetChunkPillar, new: ClientAuraNetChunkPillar?
    ): ClientAuraNetChunkPillar {
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

    private fun getChunkPillar(chunkX: Int, chunkZ: Int): ClientAuraNetChunkPillar? {
        if (isInRadius(chunkX, chunkZ)) {
            val chunk = chunks.get(getIndex(chunkX, chunkZ))
            if (positionEquals(chunk, chunkX, chunkZ)) {
                return chunk
            }
        }

        return null
    }

    private fun getChunk(pos: ChunkSectionPos): ClientAuraNetChunk? {
        getChunkPillar(pos.x, pos.z)?.let { pillar ->
            // TODO: Adjust this in 1.17
            if (pos.y < 0 || pos.y > pillar.chunks.size) return null

            return pillar.chunks[pos.y]
        }

        return null
    }
}