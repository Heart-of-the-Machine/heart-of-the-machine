package com.github.hotm.gen.feature.decorator

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.gen.decorator.DecoratorConfig

/**
 * Config for the HeightmapWithinRangeDecorator.
 */
data class CountHeightmapInRangeDecoratorConfig(val minHeight: Int, val maxHeight: Int, val count: Int) :
    DecoratorConfig {
    companion object {
        val CODEC: Codec<CountHeightmapInRangeDecoratorConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CountHeightmapInRangeDecoratorConfig> ->
                instance.group(
                    Codec.INT.fieldOf("min_height").forGetter { it.minHeight },
                    Codec.INT.fieldOf("max_height").forGetter { it.maxHeight },
                    Codec.INT.fieldOf("count").forGetter { it.count }
                ).apply(instance) { minHeight, maxHeight, count ->
                    CountHeightmapInRangeDecoratorConfig(
                        minHeight,
                        maxHeight,
                        count
                    )
                }
            }
    }
}