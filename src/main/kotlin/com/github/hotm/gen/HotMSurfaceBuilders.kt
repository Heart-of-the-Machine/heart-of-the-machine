package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig

/**
 * Contains surface configs for the Heart of the Machine biomes.
 */
object HotMSurfaceBuilders {
    val RUSTED_SURFACE_BLOCK = HotMBlocks.RUSTED_MACHINE_CASING
    val SURFACE_BLOCK = HotMBlocks.SURFACE_MACHINE_CASING

    private val RUSTED_SURFACE_STATE = RUSTED_SURFACE_BLOCK.defaultState
    private val SURFACE_STATE = SURFACE_BLOCK.defaultState

    /**
     * Thinking Forest biome surface builder.
     */
    val THINKING_FOREST = register(
        "thinking_forest",
        SurfaceBuilder.DEFAULT.method_30478(TernarySurfaceConfig(SURFACE_STATE, SURFACE_STATE, SURFACE_STATE))
    )

    /**
     * Wasteland biome surface builder.
     */
    val WASTELAND = register(
        "wasteland",
        SurfaceBuilder.DEFAULT.method_30478(TernarySurfaceConfig(RUSTED_SURFACE_STATE, SURFACE_STATE, SURFACE_STATE))
    )

    /**
     * Used for statically registering configured surface builders.
     */
    private fun <SC : SurfaceConfig> register(
        name: String,
        builder: ConfiguredSurfaceBuilder<SC>
    ): ConfiguredSurfaceBuilder<SC> {
        return Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, HotMConstants.identifier(name), builder)
    }
}