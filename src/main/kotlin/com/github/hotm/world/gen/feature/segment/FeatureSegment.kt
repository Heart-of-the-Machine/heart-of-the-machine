package com.github.hotm.world.gen.feature.segment

import com.github.hotm.misc.HotMRegistries
import com.github.hotm.util.CardinalDirection
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import java.util.*

/**
 * Handles generating a specific part of a feature.
 */
interface FeatureSegment<C> {
    companion object {
        val UNIT_CODEC: Codec<FeatureSegment<Unit>> by lazy {
            HotMRegistries.UNIT_FEATURE_SEGMENT_TYPE.dispatch(
                FeatureSegment<Unit>::type,
                FeatureSegmentType<Unit, out FeatureSegment<Unit>>::codec
            )
        }
        val CARDINAL_CODEC: Codec<FeatureSegment<CardinalDirection>> by lazy {
            HotMRegistries.CARDINAL_FEATURE_SEGMENT_TYPE.dispatch(
                FeatureSegment<CardinalDirection>::type,
                FeatureSegmentType<CardinalDirection, out FeatureSegment<CardinalDirection>>::codec
            )
        }
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