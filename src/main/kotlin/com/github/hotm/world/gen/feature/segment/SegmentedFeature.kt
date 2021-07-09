package com.github.hotm.world.gen.feature.segment

import com.github.hotm.HotMProperties
import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext
import java.util.*

/**
 * A feature that generates in parts.
 */
class SegmentedFeature(codec: Codec<SegmentedFeatureConfig>) : Feature<SegmentedFeatureConfig>(codec) {
    companion object {
        private val MAX_SEGMENTS = 128
    }

    override fun generate(ctx: FeatureContext<SegmentedFeatureConfig>): Boolean {
        val world = ctx.world

        val blocks = Maps.newHashMap<BlockPos, BlockPlacement>()
        val segments = Queues.newArrayDeque<PositionedFeatureSegment<*>>()
        segments.add(PositionedFeatureSegment(ctx.origin, ctx.config.initial, Unit))

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
                    ctx.generator,
                    ctx.random
                )
            ) {
                return false
            }

            segmentCount++
        }

        val sources = mutableSetOf<BlockPos>()
        val leaves = mutableSetOf<BlockPos>()

        for (placement in blocks) {
            if (placement.value.replaceTerrain || world.isAir(placement.key)) {
                world.setBlockState(placement.key, placement.value.state, placement.value.flags)

                when (placement.value.leafPlacement) {
                    LeafPlacement.SOURCE -> sources.add(placement.key)
                    LeafPlacement.LEAF -> leaves.add(placement.key)
                    else -> {}
                }
            }
        }

        if (sources.isNotEmpty() && leaves.isNotEmpty()) {
            updateLeaves(world, sources, leaves)
        }

        return true
    }

    private fun updateLeaves(world: StructureWorldAccess, sources: Set<BlockPos>, leaves: Set<BlockPos>) {
        val layers = Array<MutableSet<BlockPos>>(HotMProperties.MAX_DISTANCE - 1) { hashSetOf() }
        val mutable = BlockPos.Mutable()

        for (source in sources) {
            for (dir in Direction.values()) {
                mutable.set(source, dir)
                if (!sources.contains(mutable)) {
                    val blockState = world.getBlockState(mutable)
                    if (blockState.contains(HotMProperties.DISTANCE)) {
                        layers[0].add(mutable.toImmutable())
                        world.setBlockState(mutable, blockState.with(HotMProperties.DISTANCE, 1), 19)
                    }
                }
            }
        }

        for (layer in 1 until (HotMProperties.MAX_DISTANCE - 1)) {
            val curLayer = layers[layer - 1]
            val nextLayer = layers[layer]

            for (leaf in curLayer) {
                for (dir in Direction.values()) {
                    mutable.set(leaf, dir)
                    if (!curLayer.contains(mutable) && !nextLayer.contains(mutable) && leaves.contains(mutable)) {
                        val blockState = world.getBlockState(mutable)
                        if (blockState.contains(HotMProperties.DISTANCE)) {
                            val distance = blockState[HotMProperties.DISTANCE]
                            if (distance > layer + 1) {
                                nextLayer.add(mutable.toImmutable())
                                world.setBlockState(mutable, blockState.with(HotMProperties.DISTANCE, layer + 1), 19)
                            }
                        }
                    }
                }
            }
        }
    }
}