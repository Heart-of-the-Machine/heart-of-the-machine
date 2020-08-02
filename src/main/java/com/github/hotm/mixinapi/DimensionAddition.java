package com.github.hotm.mixinapi;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

/**
 * Describes a dimension to be added.
 */
public class DimensionAddition {
    private final RegistryKey<DimensionOptions> optionsRegistryKey;
    private final RegistryKey<DimensionType> typeRegistryKey;
    private final DimensionType dimensionType;
    private final ChunkGeneratorSupplier chunkGeneratorSupplier;

    public DimensionAddition(RegistryKey<DimensionOptions> optionsRegistryKey, RegistryKey<DimensionType> typeRegistryKey, DimensionType dimensionType, ChunkGeneratorSupplier chunkGeneratorSupplier) {
        this.optionsRegistryKey = optionsRegistryKey;
        this.typeRegistryKey = typeRegistryKey;
        this.dimensionType = dimensionType;
        this.chunkGeneratorSupplier = chunkGeneratorSupplier;
    }

    public RegistryKey<DimensionOptions> getOptionsRegistryKey() {
        return optionsRegistryKey;
    }

    public RegistryKey<DimensionType> getTypeRegistryKey() {
        return typeRegistryKey;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public ChunkGeneratorSupplier getChunkGeneratorSupplier() {
        return chunkGeneratorSupplier;
    }
}
