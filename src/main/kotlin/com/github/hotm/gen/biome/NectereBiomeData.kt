package com.github.hotm.gen.biome

import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

data class NectereBiomeData(
    val biome: RegistryKey<Biome>,
    val coordinateMultiplier: Double,
    val targetWorld: RegistryKey<World>,
    val isPortalable: Boolean
)
