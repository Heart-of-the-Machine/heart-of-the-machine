package com.github.hotm.world.auranet

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

class AuraNetStorage(private val serverWorld: ServerWorld, file: File, dataFixer: DataFixer, dsync: Boolean) :
    CustomSerializingRegionBasedStorage<AuraNetData>(
        file,
        { AuraNetData.createCodec(it, serverWorld.registryKey) },
        { AuraNetData(it, serverWorld.registryKey) },
        dataFixer,
        null,
        dsync
    ) {

    private val signalExector = Executors.newSingleThreadExecutor { Thread().apply { isDaemon = true } }

    fun getBaseAura(pos: ChunkSectionPos): Int {
        return getOrCreate(pos.asLong()).baseValue
    }

    fun setBaseAura(pos: ChunkSectionPos, baseAura: Int) {
        getOrCreate(pos.asLong()).baseValue = baseAura
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

    fun initForPalette(chunkPos: ChunkPos, section: ChunkSection) {
        val sectionPos = ChunkSectionPos.from(chunkPos, section.yOffset shr 4)
        Util.ifPresentOrElse(get(sectionPos.asLong()), { data ->
            data.updateAuraNodes(signalExector, serverWorld, sectionPos) { callback ->
                if (shouldScan(section)) {
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        }, {
            if (shouldScan(section)) {
                getOrCreate(sectionPos.asLong()).updateAuraNodes(signalExector, serverWorld, sectionPos) { callback ->
                    scanAndPopulate(sectionPos, section, callback)
                }
            }
        })
    }

    private fun shouldScan(section: ChunkSection): Boolean {
        return section.hasAny(AuraNodes::containsState)
    }

    private fun scanAndPopulate(
        sectionPos: ChunkSectionPos,
        section: ChunkSection,
        update: (BlockPos, BlockState, AuraNode) -> Unit
    ) {
        for (pos in sectionPos.streamBlocks()) {
            val state = section.getBlockState(
                ChunkSectionPos.getLocalCoord(pos.x),
                ChunkSectionPos.getLocalCoord(pos.y),
                ChunkSectionPos.getLocalCoord(pos.z)
            )
            AuraNodes.tryCreateAuraNode(state)?.let { update(pos, state, it) }
        }
    }
}