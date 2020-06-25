package com.github.hotm.gen.feature.decorator

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.gen.decorator.DecoratorConfig

data class CountChanceSurfaceInRangeDecoratorConfig(
    val minHeight: Int,
    val maxHeight: Int,
    val count: Int,
    val chance: Float
) : DecoratorConfig {
    companion object {
        val CODEC: Codec<CountChanceSurfaceInRangeDecoratorConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CountChanceSurfaceInRangeDecoratorConfig> ->
                instance.group(
                    Codec.INT.fieldOf("min_height").forGetter { it.minHeight },
                    Codec.INT.fieldOf("max_height").forGetter { it.maxHeight },
                    Codec.INT.fieldOf("count").forGetter { it.count },
                    Codec.FLOAT.fieldOf("chance").forGetter { it.chance }
                ).apply(instance) { minHeight, maxHeight, count, chance ->
                    CountChanceSurfaceInRangeDecoratorConfig(minHeight, maxHeight, count, chance)
                }
            }
    }
}