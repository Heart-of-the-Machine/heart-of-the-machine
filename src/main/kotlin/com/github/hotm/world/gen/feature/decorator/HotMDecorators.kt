package com.github.hotm.world.gen.feature.decorator

import com.github.hotm.HotMConstants
import com.github.hotm.world.gen.feature.decorator.*
import net.minecraft.util.registry.Registry

/**
 * Keeps track of the Heart of the Machine decorators.
 */
object HotMDecorators {
    lateinit var COUNT_HEIGHTMAP_IN_RANGE: CountHeightmapInRangeDecorator
        private set
    lateinit var COUNT_CHANCE_SURFACE_IN_RANGE: CountChanceSurfaceInRangeDecorator
        private set
    lateinit var COUNT_CHANCE_HEIGHTMAP_IN_RANGE: CountChanceHeightmapInRangeDecorator
        private set

    fun register() {
        COUNT_HEIGHTMAP_IN_RANGE = Registry.register(
            Registry.DECORATOR,
            HotMConstants.identifier("count_heightmap_in_range"),
            CountHeightmapInRangeDecorator(CountHeightmapInRangeDecoratorConfig.CODEC)
        )
        COUNT_CHANCE_SURFACE_IN_RANGE = Registry.register(
            Registry.DECORATOR,
            HotMConstants.identifier("count_chance_surface_in_range"),
            CountChanceSurfaceInRangeDecorator(CountChanceInRangeDecoratorConfig.CODEC)
        )
        COUNT_CHANCE_HEIGHTMAP_IN_RANGE = Registry.register(
            Registry.DECORATOR,
            HotMConstants.identifier("count_chance_heightmap_in_range"),
            CountChanceHeightmapInRangeDecorator(CountChanceInRangeDecoratorConfig.CODEC)
        )
    }
}