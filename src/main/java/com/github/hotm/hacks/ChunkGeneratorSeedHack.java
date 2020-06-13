package com.github.hotm.hacks;

/**
 * This is a hack to retrieve the seed of the currently open world.
 * <p>
 * This works in conjunction with SurfaceChunkGeneratorMixin.
 */
public class ChunkGeneratorSeedHack {
    /**
     * The seed of the currently open world.
     * <p>
     * This value is set every time a SurfaceChunkGenerator is initialized.
     */
    public static long seed;
}
