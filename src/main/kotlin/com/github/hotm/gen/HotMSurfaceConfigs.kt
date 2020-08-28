package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig

/**
 * Contains surface configs for the Heart of the Machine biomes.
 */
object HotMSurfaceConfigs {
    val RUSTED_SURFACE_BLOCK = HotMBlocks.RUSTED_MACHINE_CASING
    val SURFACE_BLOCK = HotMBlocks.SURFACE_MACHINE_CASING

    private val RUSTED_SURFACE_STATE = RUSTED_SURFACE_BLOCK.defaultState
    private val SURFACE_STATE = SURFACE_BLOCK.defaultState

    /**
     * Thinking forest biome surface config.
     */
    val THINKING_FOREST_CONFIG = TernarySurfaceConfig(SURFACE_STATE, SURFACE_STATE, SURFACE_STATE)

    /**
     * Wasteland biome surface config.
     */
    val WASTELAND_CONFIG = TernarySurfaceConfig(RUSTED_SURFACE_STATE, SURFACE_STATE, SURFACE_STATE)
}