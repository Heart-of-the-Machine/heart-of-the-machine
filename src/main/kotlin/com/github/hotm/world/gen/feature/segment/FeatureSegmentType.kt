package com.github.hotm.world.gen.feature.segment

import com.github.hotm.HotMConstants
import com.github.hotm.HotMRegistries
import com.github.hotm.util.CardinalDirection
import com.mojang.serialization.Codec
import net.minecraft.util.registry.Registry

/**
 * A type of feature part.
 */
class FeatureSegmentType<C, S : FeatureSegment<C>>(val codec: Codec<S>) {
    companion object {
        val PLASSEIN_STEM_FEATURE_SEGMENT =
            registerUnit("plassein_stem_feature_segment", PlasseinStemSegment.CODEC)
        val PLASSEIN_BRANCH_FEATURE_SEGMENT =
            registerCardinal("plassein_branch_feature_segment", PlasseinBranchSegment.CODEC)
        val PLASSEIN_LEAF_FEATURE_SEGMENT =
            registerUnit("plassein_leaf_feature_segment", PlasseinLeafSegment.CODEC)

        private fun <S : FeatureSegment<Unit>> registerUnit(
            name: String,
            codec: Codec<S>
        ): FeatureSegmentType<Unit, S> {
            return Registry.register(
                HotMRegistries.UNIT_FEATURE_SEGMENT_TYPE,
                HotMConstants.identifier(name),
                FeatureSegmentType(codec)
            )
        }

        private fun <S : FeatureSegment<CardinalDirection>> registerCardinal(
            name: String,
            codec: Codec<S>
        ): FeatureSegmentType<CardinalDirection, S> {
            return Registry.register(
                HotMRegistries.CARDINAL_FEATURE_SEGMENT_TYPE,
                HotMConstants.identifier(name),
                FeatureSegmentType(codec)
            )
        }
    }
}