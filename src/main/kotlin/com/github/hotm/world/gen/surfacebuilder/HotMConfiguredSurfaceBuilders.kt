package com.github.hotm.world.gen.surfacebuilder

import com.github.hotm.HotMConstants
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig

/**
 * Contains surface configs for the Heart of the Machine biomes.
 */
object HotMConfiguredSurfaceBuilders {

    /**
     * Thinking Forest biome surface builder.
     */
    lateinit var THINKING_FOREST: ConfiguredSurfaceBuilder<NectereSurfaceConfig>

    /**
     * Wasteland biome surface builder.
     */
    lateinit var WASTELAND: ConfiguredSurfaceBuilder<NectereSurfaceConfig>

    fun register() {
        THINKING_FOREST =
            register("thinking_forest", HotMSurfaceBuilders.PARTIAL.withConfig(HotMSurfaceBuilders.GRASS_CONFIG))
        WASTELAND = register("wasteland", HotMSurfaceBuilders.DEFAULT.withConfig(HotMSurfaceBuilders.WASTELAND_CONFIG))
    }

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