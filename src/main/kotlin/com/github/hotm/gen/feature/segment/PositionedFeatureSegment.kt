package com.github.hotm.gen.feature.segment

import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import java.util.*

/**
 * Describes a feature segment to be generated at a specific location.
 */
data class PositionedFeatureSegment<C>(val pos: BlockPos, val segment: FeatureSegment<C>, val context: C) {
    fun tryGenerate(
        placements: MutableMap<BlockPos, BlockPlacement>,
        children: MutableCollection<PositionedFeatureSegment<*>>,
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random
    ): Boolean {
        return segment.tryGenerate(placements, children, world, generator, random, pos, context)
    }
}