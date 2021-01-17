package com.github.hotm.world.auranet

import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.blocks.SiphonAuraNodeBlock
import com.github.hotm.blocks.SourceAuraNodeBlock
import com.github.hotm.world.storage.CustomSerializingRegionBasedStorage
import com.mojang.datafixers.DataFixer
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.chunk.ChunkSection
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.function.Predicate
import java.util.stream.Stream

class AuraNetStorage(private val serverWorld: ServerWorld, file: File, dataFixer: DataFixer, dsync: Boolean) :
    CustomSerializingRegionBasedStorage<AuraNetData>(
        file,
        { AuraNetData.createCodec(it, serverWorld.registryKey) },
        { AuraNetData(it, serverWorld.registryKey) },
        dataFixer,
        null,
        dsync
    ) {

    private val signalExector = Executors.newSingleThreadExecutor {
        Thread(it).apply {
            isDaemon = true
            name = "ANS-${serverWorld.registryKey.value}"
        }
    }

    fun getBaseAura(pos: ChunkSectionPos): Int {
        return getOrCreate(pos.asLong()).getBaseAura()
    }

    fun setBaseAura(pos: ChunkSectionPos, baseAura: Int) {
        getOrCreate(pos.asLong()).setBaseAura(serverWorld, this, baseAura)
    }

    fun set(pos: BlockPos, node: AuraNode) {
        getOrCreate(ChunkSectionPos.from(pos).asLong()).set(pos, node)
    }

    operator fun get(pos: BlockPos): Optional<AuraNode> {
        return get(ChunkSectionPos.from(pos).asLong()).flatMap { it[pos] }
    }

    fun remove(pos: BlockPos) {
        getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos)
    }

    fun getAllBy(pos: ChunkSectionPos, filter: Predicate<PositionedAuraNode>): Stream<PositionedAuraNode> {
        return getOrCreate(pos.asLong()).getAllBy(filter)
    }

    fun calculateSectionAura(pos: ChunkSectionPos): Int {
        val data = getOrCreate(pos.asLong())
        return data.getBaseAura() + data.getAllBy { it.node.block is SourceAuraNodeBlock }.mapToInt {
            (it.node.block as SourceAuraNodeBlock).getSource(
                serverWorld.getBlockState(it.pos),
                serverWorld,
                it.pos
            )
        }.sum()
    }

    fun calculateSiphonValue(pos: BlockPos, initDenom: Int, finalDenom: Int): Int {
        val sectionPos = ChunkSectionPos.from(pos)
        val baseAura = calculateSectionAura(sectionPos)
        val siphonCount =
            getOrCreate(sectionPos.asLong()).getAllBy { it.node.block is SiphonAuraNodeBlock }.count().toInt()

        return baseAura / (finalDenom * siphonCount + initDenom - finalDenom)
    }

    fun initForPalette(chunkPos: ChunkPos, section: ChunkSection) {
        val sectionPos = ChunkSectionPos.from(chunkPos, section.yOffset shr 4)
        Util.ifPresentOrElse(get(sectionPos.asLong()), { data ->
            data.updateAuraNodes(signalExector, serverWorld) { callback ->
                if (shouldScan(section)) {
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        }, {
            if (shouldScan(section)) {
                getOrCreate(sectionPos.asLong()).updateAuraNodes(signalExector, serverWorld) { callback ->
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
        update: (BlockState, PositionedAuraNode) -> Unit
    ) {
        for (pos in sectionPos.streamBlocks()) {
            val state = section.getBlockState(
                ChunkSectionPos.getLocalCoord(pos.x),
                ChunkSectionPos.getLocalCoord(pos.y),
                ChunkSectionPos.getLocalCoord(pos.z)
            )
            (state.block as? AuraNodeBlock)?.let {
                val immutablePos = pos.toImmutable()
                update(
                    state,
                    PositionedAuraNode(immutablePos, it.createAuraNode(state, serverWorld.registryKey, immutablePos))
                )
            }
        }
    }
}