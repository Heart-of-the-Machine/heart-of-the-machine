package com.github.hotm.world.meta.server

import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.blocks.BlockWithMeta
import com.github.hotm.net.HotMNetwork
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.meta.MetaAccess
import com.github.hotm.meta.MetaBlock
import com.github.hotm.world.storage.CustomSerializingRegionBasedStorage
import com.mojang.datafixers.DataFixer
import com.mojang.serialization.Codec
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.chunk.ChunkSection
import java.io.File
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream

class ServerMetaStorage(override val world: ServerWorld, file: File, dataFixer: DataFixer, dsync: Boolean) :
    CustomSerializingRegionBasedStorage<ServerMetaChunk>(
        file,
        dataFixer,
        null,
        dsync,
        world
    ), MetaAccess {

    override val isClient = false

    override fun factory(updateListener: Runnable): ServerMetaChunk {
        return ServerMetaChunk(updateListener, world.registryKey)
    }

    override fun codecFactory(updateListener: Runnable): Codec<ServerMetaChunk> {
        return ServerMetaChunk.createCodec(this, updateListener, world.registryKey)
    }

    /*
     * MetaAccess functions.
     */
    override fun getUpdateListener(pos: ChunkSectionPos): Runnable {
        return getOrCreate(pos.asLong()).updateListener
    }

    override fun getBaseAura(pos: ChunkSectionPos): Float {
        return get(pos.asLong()).map { it.getBaseAura() }
            .orElseGet { ServerMetaChunk.getBaseAura(world.registryKey) }
    }

    override operator fun get(pos: BlockPos): MetaBlock? {
        val chunk = get(ChunkSectionPos.from(pos).asLong())
        return if (chunk.isPresent) {
            chunk.get()[pos]
        } else {
            null
        }
    }

    override fun getAllBy(pos: ChunkSectionPos, filter: Predicate<MetaBlock>): Stream<MetaBlock> {
        return getOrCreate(pos.asLong()).getAllBy(filter)
    }

    override fun recalculateSiphons(pos: ChunkSectionPos, visitedNodes: MutableSet<DimBlockPos>) {
        getOrCreate(pos.asLong()).recalculateSiphons(visitedNodes)
    }

    /*
     * Primary mutator functions.
     */

    fun setBaseAura(pos: ChunkSectionPos, baseAura: Float) {
        getOrCreate(pos.asLong()).setBaseAura(baseAura)
        sendBaseAuraUpdate(pos, baseAura)
    }

    fun put(node: MetaBlock) {
        getOrCreate(ChunkSectionPos.from(node.pos).asLong()).put(node)
        sendMetaBlockPut(node)
    }

    fun remove(pos: BlockPos) {
        getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos)
        sendMetaBlockRemove(pos)
    }

    /*
     * Networking functions.
     */

    fun sendChunkPillar(pos: ChunkPos, buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeInt(pos.x)
        buf.writeInt(pos.z)

        val bitset = BitSet()
        val bytes = NetByteBuf.buffer()
        for (y in world.bottomSectionCoord until world.topSectionCoord) {
            val sectionPos = ChunkSectionPos.asLong(pos.x, y, pos.z)
            val optionalSection = get(sectionPos)

            if (optionalSection.isPresent) {
                val section = optionalSection.get()
                ServerMetaChunk.toPacket(bytes, ctx, section)
                bitset.set(y - world.bottomSectionCoord)
            }
        }

        buf.writeBitSet(bitset)
        buf.writeBytes(bytes)
    }

    private fun sendMetaBlockPut(node: MetaBlock) {
        HotMNetwork.sendMetaBlockPut(world, node.pos) { buf, ctx ->
            buf.writeBlockPos(node.pos)
            MetaBlock.toPacketNoPos(node, buf, ctx)
        }
    }

    private fun sendMetaBlockRemove(pos: BlockPos) {
        HotMNetwork.sendMetaBlockRemove(world, pos) { buf, _ ->
            buf.writeBlockPos(pos)
        }
    }

    private fun sendBaseAuraUpdate(pos: ChunkSectionPos, baseAura: Float) {
        HotMNetwork.sendBaseAuraUpdate(world, pos) { buf, _ ->
            buf.writeVarUnsignedInt(pos.x)
            buf.writeVarUnsignedInt(pos.y)
            buf.writeVarUnsignedInt(pos.z)
            buf.writeFloat(baseAura)
        }
    }

    /*
     * Calculation functions.
     */

    fun getSectionAura(pos: ChunkSectionPos): Float {
        return get(pos.asLong()).map { it.getTotalAura() }
            .orElseGet { ServerMetaChunk.getBaseAura(world.registryKey) }
    }

    /*
     * Init functions.
     */

    fun initForPalette(chunkPos: ChunkPos, section: ChunkSection) {
        val sectionPos = ChunkSectionPos.from(chunkPos, section.yOffset shr 4)
        Util.ifPresentOrElse(get(sectionPos.asLong()), { data ->
            data.updateMetaBlocks(world, this) { callback ->
                if (shouldScan(section)) {
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        }, {
            if (shouldScan(section)) {
                getOrCreate(sectionPos.asLong()).updateMetaBlocks(world, this) { callback ->
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        })
    }

    private fun shouldScan(section: ChunkSection): Boolean {
        return section.hasAny { it.block is BlockWithMeta }
    }

    private fun scanAndPopulate(
        sectionPos: ChunkSectionPos,
        section: ChunkSection,
        update: (BlockState, BlockWithMeta, BlockPos) -> Unit
    ) {
        for (pos in sectionPos.streamBlocks()) {
            val state = section.getBlockState(
                ChunkSectionPos.getLocalCoord(pos.x),
                ChunkSectionPos.getLocalCoord(pos.y),
                ChunkSectionPos.getLocalCoord(pos.z)
            )
            (state.block as? BlockWithMeta)?.let { block ->
                val immutablePos = pos.toImmutable()
                update(state, block, immutablePos)
            }
        }
    }
}