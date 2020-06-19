package com.github.hotm.mixinopts;

import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * Supplies dimension option for the given seed.
 */
public interface ChunkGeneratorSupplier {
    ChunkGenerator getChunkGenerator(long seed);
}
