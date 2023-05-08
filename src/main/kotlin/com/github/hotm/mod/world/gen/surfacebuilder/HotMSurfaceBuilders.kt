package com.github.hotm.mod.world.gen.surfacebuilder

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object HotMSurfaceBuilders {
    fun init() {
        Registry.register(Registries.MATERIAL_CONDITION, id("density_threshold"), DensityThresholdCondition.CODEC.codec)
    }
}
