package com.github.hotm.gen.biome

import com.github.hotm.gen.HotMSurfaceConfigs
import com.github.hotm.gen.feature.HotMBiomeFeatures
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder

class WastelandBiome : Biome(
    Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, HotMSurfaceConfigs.WASTELAND_CONFIG)
        .precipitation(Precipitation.NONE).category(Category.PLAINS).depth(0.125F).scale(0.05F)
        .temperature(0.8F).downfall(0.0F).effects(
            BiomeEffects.Builder().waterColor(0x7591c7).waterFogColor(0x050533).fogColor(0x222222).moodSound(
                BiomeMoodSound.CAVE
            ).build()
        )
        .parent(null)
        .noises(listOf(MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 1.0f)))
), NectereBiome {
    init {
        HotMBiomeFeatures.addRefusePiles(this)
        HotMBiomeFeatures.addCrystalGrowths(this)
    }

    override val coordinateMultiplier = 1.0
    override val targetWorld: RegistryKey<World> = World.OVERWORLD
    override val isPortalable = true
}
