package com.github.hotm.gen.biome

import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

interface NectereBiome {
    val coordinateMultiplier: Double

    val targetWorld: RegistryKey<World>

    val isPortalable: Boolean
}