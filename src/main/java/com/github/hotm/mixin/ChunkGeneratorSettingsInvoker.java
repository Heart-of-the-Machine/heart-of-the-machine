package com.github.hotm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGeneratorSettings.class)
public interface ChunkGeneratorSettingsInvoker {
    @Invoker("<init>")
    static ChunkGeneratorSettings create(StructuresConfig structuresConfig, GenerationShapeConfig generationShapeConfig,
                                         BlockState defaultBlock, BlockState defaultFluid, int bedrockCeilingY,
                                         int bedrockFloorY, int seaLevel, boolean mobGenerationDisabled) {
        throw new RuntimeException("ChunkGeneratorSettingsInvoker mixin was not mixed in properly!");
    }
}
