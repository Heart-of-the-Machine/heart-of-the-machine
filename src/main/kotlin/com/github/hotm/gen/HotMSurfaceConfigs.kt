package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig

/**
 * Contains surface configs for the Heart of the Machine biomes.
 */
object HotMSurfaceConfigs {
    val SURFACE_BLOCK = HotMBlocks.TEST_MACHINE_CASING
    val SUBSURFACE_BLOCK = HotMBlocks.METAL_MACHINE_CASING

    private val SURFACE_STATE = SURFACE_BLOCK.defaultState
    private val SUBSURFACE_STATE = SUBSURFACE_BLOCK.defaultState

    /**
     * Thinking forest biome surface config.
     */
    val THINKING_FOREST_CONFIG = TernarySurfaceConfig(SURFACE_STATE, SUBSURFACE_STATE, SUBSURFACE_STATE)
}