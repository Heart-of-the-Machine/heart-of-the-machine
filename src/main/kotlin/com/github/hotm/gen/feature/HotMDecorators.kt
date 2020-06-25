package com.github.hotm.gen.feature

import com.github.hotm.HotMConstants
import com.github.hotm.gen.feature.decorator.CountChanceSurfaceInRangeDecorator
import com.github.hotm.gen.feature.decorator.CountChanceSurfaceInRangeDecoratorConfig
import com.github.hotm.gen.feature.decorator.CountHeightmapInRangeDecorator
import com.github.hotm.gen.feature.decorator.CountHeightmapInRangeDecoratorConfig
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Keeps track of the Heart of the Machine decorators.
 */
object HotMDecorators {
    val COUNT_HEIGHTMAP_IN_RANGE: CountHeightmapInRangeDecorator = Registry.register(
        Registry.DECORATOR,
        Identifier(HotMConstants.MOD_ID, "count_heightmap_in_range"),
        CountHeightmapInRangeDecorator(CountHeightmapInRangeDecoratorConfig.CODEC)
    )

    val COUNT_CHANCE_SURFACE_IN_RANGE: CountChanceSurfaceInRangeDecorator = Registry.register(
        Registry.DECORATOR,
        Identifier(HotMConstants.MOD_ID, "count_chance_surface_in_range"),
        CountChanceSurfaceInRangeDecorator(CountChanceSurfaceInRangeDecoratorConfig.CODEC)
    )
}