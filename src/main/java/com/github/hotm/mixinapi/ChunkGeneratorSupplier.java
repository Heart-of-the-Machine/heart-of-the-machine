package com.github.hotm.mixinapi;

import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * Supplies dimension option for the given seed.
 */
public interface ChunkGeneratorSupplier {
    ChunkGenerator getChunkGenerator(long seed);
}
