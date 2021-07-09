package com.github.hotm.world.gen.feature

import com.github.hotm.HotMConstants
import com.github.hotm.mixinapi.FeatureAdditions
import com.google.common.collect.ImmutableMap
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.feature.ConfiguredStructureFeature
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.StructureFeature

/**
 * Manages Heart of the Machine's Structure Features. (There will be a lot eventually)
 */
object HotMStructureFeatures {
    val NECTERE_PORTAL = FeatureAdditions.registerStructure(
        "hotm:nectere_portal", // NOTE: This should have been: "hotm:nectere_portal"
        NecterePortalStructureFeature(DefaultFeatureConfig.CODEC),
        GenerationStep.Feature.SURFACE_STRUCTURES
    )

    val NECTERE_PORTAL_CONFIG = StructureConfig(32, 8, 103873)

    /**
     * Configured Nectere portal structure feature. (Only intended to be used in the Nectere dimension.)
     */
    val NECTERE_SIDE_NECTERE_PORTAL = register("nectere_portal", NECTERE_PORTAL.configure(DefaultFeatureConfig.INSTANCE))

    /**
     * Called from StructuresConfig's static initializer.
     */
    fun addConfigs(config: ImmutableMap.Builder<StructureFeature<*>, StructureConfig>) {
        config.put(NECTERE_PORTAL, NECTERE_PORTAL_CONFIG)
    }

    fun register() {
        HotMStructurePieces.register()
    }

    fun <FC : FeatureConfig, F : StructureFeature<FC>> register(id: String, configuredStructureFeature: ConfiguredStructureFeature<FC, F>): ConfiguredStructureFeature<FC, F> {
        return Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, HotMConstants.identifier(id), configuredStructureFeature)
    }
}