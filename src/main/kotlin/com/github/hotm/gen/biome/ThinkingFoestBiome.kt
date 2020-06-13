package com.github.hotm.gen.biome

import net.minecraft.sound.BiomeMoodSound
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder

class ThinkingFoestBiome : Biome(
    Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
        .precipitation(Precipitation.RAIN).category(Category.JUNGLE).depth(0.45F).scale(0.3F)
        .temperature(0.95F).downfall(0.9F).effects(
            (BiomeEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(
                BiomeMoodSound.CAVE
            ).build()
        ).parent(null)
) {
}