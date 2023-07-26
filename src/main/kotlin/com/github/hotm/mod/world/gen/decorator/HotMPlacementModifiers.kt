package com.github.hotm.mod.world.gen.decorator

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.decorator.PlacementModifierType

object HotMPlacementModifiers {
    val RANDOM_SURFACE_IN_RANGE by lazy { PlacementModifierType { RandomSurfaceInRangePlacementModifier.MODIFIER_CODEC } }

    fun init() {
        Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, id("random_surface_in_range"), RANDOM_SURFACE_IN_RANGE)
    }
}
