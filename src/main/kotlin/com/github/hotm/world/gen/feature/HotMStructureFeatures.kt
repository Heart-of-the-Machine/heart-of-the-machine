package com.github.hotm.world.gen.feature

import com.github.hotm.HotMConstants
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredStructureFeature
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.StructureFeature

/**
 * Manages Heart of the Machine's Structure Features. (There will be a lot eventually)
 */
object HotMStructureFeatures {
    lateinit var NECTERE_PORTAL: NecterePortalStructureFeature
        private set

    /**
     * Configured Nectere portal structure feature. (Only intended to be used in the Nectere dimension.)
     */
    lateinit var NECTERE_SIDE_NECTERE_PORTAL: ConfiguredStructureFeature<DefaultFeatureConfig, out StructureFeature<DefaultFeatureConfig>>
        private set

    fun register() {
        HotMStructurePieces.register()

        NECTERE_PORTAL = NecterePortalStructureFeature(DefaultFeatureConfig.CODEC)
        NECTERE_SIDE_NECTERE_PORTAL = NECTERE_PORTAL.configure(DefaultFeatureConfig.INSTANCE)

        FabricStructureBuilder.create(HotMConstants.identifier("nectere_portal"), NECTERE_PORTAL)
            .step(GenerationStep.Feature.SURFACE_STRUCTURES).defaultConfig(32, 8, 103873)
            .superflatFeature(NECTERE_SIDE_NECTERE_PORTAL).register()
        register("nectere_portal", NECTERE_SIDE_NECTERE_PORTAL)
    }

    fun <FC : FeatureConfig, F : StructureFeature<FC>> register(
        id: String,
        configuredStructureFeature: ConfiguredStructureFeature<FC, F>
    ): ConfiguredStructureFeature<FC, F> {
        return Registry.register(
            BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE,
            HotMConstants.identifier(id),
            configuredStructureFeature
        )
    }
}