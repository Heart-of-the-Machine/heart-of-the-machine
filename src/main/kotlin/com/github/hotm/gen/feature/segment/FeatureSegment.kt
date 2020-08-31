package com.github.hotm.gen.feature.segment

import com.github.hotm.HotMRegistries
import com.github.hotm.util.CardinalDirection
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import java.util.*

/**
 * Handles generating a specific part of a feature.
 */
interface FeatureSegment<C> {
    companion object {
        val UNIT_CODEC: Codec<FeatureSegment<Unit>> =
            HotMRegistries.UNIT_FEATURE_SEGMENT_TYPE.dispatch<FeatureSegment<Unit>>(
                FeatureSegment<Unit>::type,
                FeatureSegmentType<Unit, out FeatureSegment<Unit>>::codec
            )
        val CARDINAL_CODEC: Codec<FeatureSegment<CardinalDirection>> =
            HotMRegistries.CARDINAL_FEATURE_SEGMENT_TYPE.dispatch<FeatureSegment<CardinalDirection>>(
                FeatureSegment<CardinalDirection>::type,
                FeatureSegmentType<CardinalDirection, out FeatureSegment<CardinalDirection>>::codec
            )
    }

    val type: FeatureSegmentType<C, *>

    fun tryGenerate(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        children: MutableCollection<PositionedFeatureSegment<*>>,
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        context: C
    ): Boolean
}