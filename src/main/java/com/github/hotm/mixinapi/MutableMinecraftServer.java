package com.github.hotm.mixinapi;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

/**
 * Interface for a minecraft server that can have worlds added to it.
 */
public interface MutableMinecraftServer {
    long hotm_getSeed();

    void hotm_addDimension(RegistryKey<DimensionOptions> optionsKey, DimensionAddition dimensionOptions);
}
