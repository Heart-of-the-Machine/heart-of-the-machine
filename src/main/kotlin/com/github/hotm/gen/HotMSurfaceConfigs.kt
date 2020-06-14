package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig

/**
 * Contains surface configs for the Heart of the Machine biomes.
 */
object HotMSurfaceConfigs {
    private val TEST_MACHINE_CASING = HotMBlocks.TEST_MACHINE_CASING.defaultState
    private val METAL_MACHINE_CASING = HotMBlocks.METAL_MACHINE_CASING.defaultState

    /**
     * Thinking forest biome surface config.
     */
    val THINKING_FOREST_CONFIG = TernarySurfaceConfig(TEST_MACHINE_CASING, METAL_MACHINE_CASING, METAL_MACHINE_CASING)
}