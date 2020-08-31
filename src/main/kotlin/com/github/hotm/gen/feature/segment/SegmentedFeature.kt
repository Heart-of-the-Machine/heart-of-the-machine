package com.github.hotm.gen.feature.segment

import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

/**
 * A feature that generates in parts.
 */
class SegmentedFeature(codec: Codec<SegmentedFeatureConfig>) : Feature<SegmentedFeatureConfig>(codec) {
    companion object {
        private val MAX_SEGMENTS = 128
    }

    override fun generate(
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: SegmentedFeatureConfig
    ): Boolean {
        val blocks = Maps.newHashMap<BlockPos, BlockPlacement>()
        val segments = Queues.newArrayDeque<PositionedFeatureSegment<*>>()
        segments.add(PositionedFeatureSegment(pos, config.initial, Unit))

        var segmentCount = 0
        while (segments.isNotEmpty()) {
            if (segmentCount >= MAX_SEGMENTS) {
                break
            }

            val segment = segments.remove()
            if (!segment.tryGenerate(
                    blocks,
                    segments,
                    world,
                    generator,
                    random
                )
            ) {
                return false
            }

            segmentCount++
        }

        for (placement in blocks) {
            if (placement.value.replaceTerrain || world.isAir(placement.key)) {
                world.setBlockState(placement.key, placement.value.state, placement.value.flags)
            }
        }

        return true
    }
}