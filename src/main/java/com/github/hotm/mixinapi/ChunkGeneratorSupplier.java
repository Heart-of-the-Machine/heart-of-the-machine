package com.github.hotm.mixinapi;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

/**
 * Supplies dimension option for the given seed.
 */
public interface ChunkGeneratorSupplier {
    ChunkGenerator getChunkGenerator(Registry<Biome> biomes,
                                     Registry<ChunkGeneratorSettings> generatorSettings,
                                     long seed);
}
