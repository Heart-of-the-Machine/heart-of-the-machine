package com.github.hotm.world.auranet.server

import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.net.HotMNetwork
import com.github.hotm.world.auranet.*
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
import java.util.function.Predicate
import java.util.stream.Stream

class ServerAuraNetStorage(override val world: ServerWorld, file: File, dataFixer: DataFixer, dsync: Boolean) :
    CustomSerializingRegionBasedStorage<ServerAuraNetChunk>(
        file,
        dataFixer,
        null,
        dsync
    ), AuraNetAccess {

    companion object {
        private val MIN_CHUNK_Y = 0
        private val MAX_CHUNK_Y = 15
    }

    override val isClient = false

    override fun factory(updateListener: Runnable): ServerAuraNetChunk {
        return ServerAuraNetChunk(updateListener, world.registryKey)
    }

    override fun codecFactory(updateListener: Runnable): Codec<ServerAuraNetChunk> {
        return ServerAuraNetChunk.createCodec(this, updateListener, world.registryKey)
    }

    /*
     * AuraNodeAccess functions.
     */

    override fun getBaseAura(pos: ChunkSectionPos): Int {
        return getOrCreate(pos.asLong()).getBaseAura()
    }

    override operator fun get(pos: BlockPos): AuraNode? {
        val chunk = get(ChunkSectionPos.from(pos).asLong())
        return if (chunk.isPresent) {
            chunk.get()[pos]
        } else {
            null
        }
    }

    override fun getAllBy(pos: ChunkSectionPos, filter: Predicate<AuraNode>): Stream<AuraNode> {
        return getOrCreate(pos.asLong()).getAllBy(filter)
    }

    /*
     * Primary mutator functions.
     */

    fun setBaseAura(pos: ChunkSectionPos, baseAura: Int) {
        getOrCreate(pos.asLong()).setBaseAura(baseAura)
        sendBaseAuraUpdate(pos, baseAura)
    }

    fun put(node: AuraNode) {
        getOrCreate(ChunkSectionPos.from(node.pos).asLong()).put(node)
        sendAuraNodePut(node)
    }

    fun remove(pos: BlockPos) {
        getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos)
        sendAuraNodeRemove(pos)
    }

    /*
     * Networking functions.
     */

    fun sendChunkPillar(pos: ChunkPos, buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeInt(pos.x)
        buf.writeInt(pos.z)

        for (y in MIN_CHUNK_Y..MAX_CHUNK_Y) {
            val sectionPos = ChunkSectionPos.from(pos, y)
            ServerAuraNetChunk.toPacket(buf, ctx, get(sectionPos.asLong()), world.registryKey)
        }
    }

    private fun sendAuraNodePut(node: AuraNode) {
        HotMNetwork.sendAuraNodePut(world, node.pos) { buf, ctx ->
            buf.writeBlockPos(node.pos)
            AuraNode.toPacketNoPos(node, buf, ctx)
        }
    }

    private fun sendAuraNodeRemove(pos: BlockPos) {
        HotMNetwork.sendAuraNodeRemove(world, pos) { buf, _ ->
            buf.writeBlockPos(pos)
        }
    }

    private fun sendBaseAuraUpdate(pos: ChunkSectionPos, baseAura: Int) {
        HotMNetwork.sendBaseAuraUpdate(world, pos) { buf, _ ->
            buf.writeVarUnsignedInt(pos.x)
            buf.writeVarUnsignedInt(pos.y)
            buf.writeVarUnsignedInt(pos.z)
            buf.writeVarUnsignedInt(baseAura)
        }
    }

    /*
     * Calculation functions.
     *
     * TODO: Figure out what to do with these.
     */

    fun calculateSectionAura(pos: ChunkSectionPos): Int {
        val data = getOrCreate(pos.asLong())
        return data.getBaseAura() + data.getAllBy { it is SourceAuraNode }.mapToInt {
            (it as SourceAuraNode).getSource()
        }.sum()
    }

    fun calculateSiphonValue(pos: BlockPos, initDenom: Int, finalDenom: Int): Int {
        val sectionPos = ChunkSectionPos.from(pos)
        val baseAura = calculateSectionAura(sectionPos)
        val siphonCount =
            getOrCreate(sectionPos.asLong()).getAllBy { it is SiphonAuraNode }.count().toInt()

        return baseAura / (finalDenom * siphonCount + initDenom - finalDenom)
    }

    /*
     * Init functions.
     */

    fun initForPalette(chunkPos: ChunkPos, section: ChunkSection) {
        val sectionPos = ChunkSectionPos.from(chunkPos, section.yOffset shr 4)
        Util.ifPresentOrElse(get(sectionPos.asLong()), { data ->
            data.updateAuraNodes(world) { callback ->
                if (shouldScan(section)) {
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        }, {
            if (shouldScan(section)) {
                getOrCreate(sectionPos.asLong()).updateAuraNodes(world) { callback ->
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        })
    }

    private fun shouldScan(section: ChunkSection): Boolean {
        return section.hasAny { it.block is AuraNodeBlock }
    }

    private fun scanAndPopulate(
        sectionPos: ChunkSectionPos,
        section: ChunkSection,
        update: (BlockState, AuraNodeBlock, BlockPos) -> Unit
    ) {
        for (pos in sectionPos.streamBlocks()) {
            val state = section.getBlockState(
                ChunkSectionPos.getLocalCoord(pos.x),
                ChunkSectionPos.getLocalCoord(pos.y),
                ChunkSectionPos.getLocalCoord(pos.z)
            )
            (state.block as? AuraNodeBlock)?.let { block ->
                val immutablePos = pos.toImmutable()
                update(state, block, immutablePos)
            }
        }
    }
}