package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.world.gen.feature.FeatureConfig

data class PileFeatureConfig(val state: BlockState, val startRadius: Int) : FeatureConfig {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PileFeatureConfig> ->
            instance.group(
                BlockState.CODEC.fieldOf("state").forGetter { it.state },
                Codec.INT.fieldOf("start_radius").forGetter { it.startRadius })
                .apply(instance) { state, startRadius -> PileFeatureConfig(state, startRadius) }
        }
    }
}
