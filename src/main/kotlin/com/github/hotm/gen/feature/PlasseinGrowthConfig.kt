package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.world.gen.feature.FeatureConfig

/**
 * Configures a PlasseinGrowthFeature.
 */
data class PlasseinGrowthConfig(
    val stalk: BlockState,
    val leaves: BlockState,
    val heightMin: Int,
    val heightVariation: Int,
    val sway: Double,
    val swayMultiplier: Double
) :
    FeatureConfig {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PlasseinGrowthConfig> ->
            instance.group(
                BlockState.CODEC.fieldOf("stalk").forGetter { it.stalk },
                BlockState.CODEC.fieldOf("leaves").forGetter { it.leaves },
                Codec.INT.fieldOf("height_min").forGetter { it.heightMin },
                Codec.INT.fieldOf("height_variation").forGetter { it.heightVariation },
                Codec.DOUBLE.fieldOf("sway").forGetter { it.sway },
                Codec.DOUBLE.fieldOf("sway_multiplier").forGetter { it.swayMultiplier }
            )
                .apply(instance) { stalk, leaves, heightMin, heightVariation, sway, swayMultiplier ->
                    PlasseinGrowthConfig(
                        stalk,
                        leaves,
                        heightMin,
                        heightVariation,
                        sway,
                        swayMultiplier
                    )
                }
        }
    }
}
