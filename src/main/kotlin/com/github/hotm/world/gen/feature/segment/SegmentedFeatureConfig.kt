package com.github.hotm.world.gen.feature.segment

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.gen.feature.FeatureConfig

/**
 * Configuration for the SegmentedFeature.
 */
data class SegmentedFeatureConfig(val initial: FeatureSegment<Unit>) : FeatureConfig {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<SegmentedFeatureConfig> ->
            instance.group(FeatureSegment.UNIT_CODEC.fieldOf("initial").forGetter(SegmentedFeatureConfig::initial))
                .apply(instance) { initial -> SegmentedFeatureConfig(initial) }
        }
    }
}