package com.github.hotm.gen.biome

import com.github.hotm.gen.HotMSurfaceConfigs
import com.github.hotm.gen.feature.HotMBiomeFeatures
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder

class ThinkingForestBiome : Biome(
    Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, HotMSurfaceConfigs.THINKING_FOREST_CONFIG)
        .precipitation(Precipitation.RAIN).category(Category.JUNGLE).depth(0.45F).scale(0.3F)
        .temperature(0.5f).downfall(0.5f).effects(
            (BiomeEffects.Builder()).waterColor(0x3f76e4).waterFogColor(0x050533).fogColor(0x222222).moodSound(
                BiomeMoodSound.CAVE
            ).build()
        ).parent(null)
) {
    init {
        HotMBiomeFeatures.addRefusePiles(this)
        HotMBiomeFeatures.addPlasseinGrowths(this)
    }
}