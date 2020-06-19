package com.github.hotm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

/**
 * Allows creation of ChunkGeneratorType objects.
 */
@Mixin(ChunkGeneratorType.class)
public interface ChunkGeneratorTypeInvoker {
    @Invoker("<init>")
    static ChunkGeneratorType create(StructuresConfig structures, NoiseConfig noise, BlockState defaultBlock,
                                     BlockState defaultFluid, int bedrockRoofPosition, int bedrockFloorPosition,
                                     int seaLevel, boolean disableMobGeneration) {
        throw new RuntimeException("ChunkGeneratorTypeInvoker mixin was not mixed in properly!");
    }

    @Invoker("<init>")
    static ChunkGeneratorType create(StructuresConfig structures, NoiseConfig noise, BlockState defaultBlock,
                                     BlockState defaultFluid, int bedrockRoofPosition, int bedrockFloorPosition,
                                     int seaLevel, boolean disableMobGeneration,
                                     Optional<ChunkGeneratorType.Preset> preset) {
        throw new RuntimeException("ChunkGeneratorTypeInvoker mixin was not mixed in properly!");
    }
}
