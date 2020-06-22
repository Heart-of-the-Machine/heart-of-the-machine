package com.github.hotm.gen.feature

import com.github.hotm.mixinopts.StructureAdditions
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.chunk.StructuresConfig
import net.minecraft.world.gen.feature.DefaultFeatureConfig

/**
 * Manages Heart of the Machine's Structure Features. (There will be a lot eventually)
 */
object HotMStructureFeatures {
    val NECTERE_PORTAL = StructureAdditions.register(
        "nectere_portal",
        NecterePortalFeature(DefaultFeatureConfig.CODEC),
        GenerationStep.Feature.SURFACE_STRUCTURES
    )

    fun register() {
        HotMStructurePieces.register()
    }
}