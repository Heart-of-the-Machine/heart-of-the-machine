package com.github.hotm.world.gen.feature.segment

import com.github.hotm.HotMConstants
import com.github.hotm.misc.HotMRegistries
import com.github.hotm.util.CardinalDirection
import com.mojang.serialization.Codec
import net.minecraft.util.registry.Registry

object HotMFeatureSegmentTypes {
    lateinit var PLASSEIN_STEM_FEATURE_SEGMENT: FeatureSegmentType<Unit, PlasseinStemSegment>
        private set
    lateinit var PLASSEIN_BRANCH_FEATURE_SEGMENT: FeatureSegmentType<CardinalDirection, PlasseinBranchSegment>
        private set
    lateinit var PLASSEIN_LEAF_FEATURE_SEGMENT: FeatureSegmentType<Unit, PlasseinLeafSegment>
        private set

    fun register() {
        PLASSEIN_STEM_FEATURE_SEGMENT = registerUnit("plassein_stem_feature_segment", PlasseinStemSegment.CODEC)
        PLASSEIN_BRANCH_FEATURE_SEGMENT =
            registerCardinal("plassein_branch_feature_segment", PlasseinBranchSegment.CODEC)
        PLASSEIN_LEAF_FEATURE_SEGMENT = registerUnit("plassein_leaf_feature_segment", PlasseinLeafSegment.CODEC)
    }

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