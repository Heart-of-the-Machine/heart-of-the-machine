package com.github.hotm.gen.feature

import com.github.hotm.mixinapi.StructureAdditions
import com.google.common.collect.ImmutableMap
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.StructureFeature

/**
 * Manages Heart of the Machine's Structure Features. (There will be a lot eventually)
 */
object HotMStructureFeatures {
    val NECTERE_PORTAL = StructureAdditions.register(
        "nectere_portal",
        NecterePortalStructureFeature(DefaultFeatureConfig.CODEC),
        GenerationStep.Feature.SURFACE_STRUCTURES
    )

    val NECTERE_PORTAL_CONFIG = StructureConfig(32, 8, 103873)

    /**
     * Called from StructuresConfig's static initializer.
     */
    fun addConfigs(config: ImmutableMap.Builder<StructureFeature<*>, StructureConfig>) {
        config.put(NECTERE_PORTAL, NECTERE_PORTAL_CONFIG)
    }

    fun register() {
        HotMStructurePieces.register()
    }
}