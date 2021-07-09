package com.github.hotm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGeneratorSettings.class)
public interface ChunkGeneratorSettingsAccessor {
    @Invoker("<init>")
    static ChunkGeneratorSettings create(StructuresConfig structuresConfig, GenerationShapeConfig generationShapeConfig,
                                         BlockState defaultBlock, BlockState defaultFluid, int bedrockCeilingY,
                                         int bedrockFloorY, int seaLevel, int minSurfaceLevel,
                                         boolean mobGenerationDisabled, boolean aquifers, boolean noiseCaves,
                                         boolean deepslate, boolean oreVeins, boolean noodleCaves) {
        throw new RuntimeException("ChunkGeneratorSettingsInvoker mixin was not mixed in properly!");
    }

    @Invoker
    boolean callHasNoiseCaves();

    @Invoker
    boolean callHasAquifers();

    @Invoker
    boolean callHasNoodleCaves();

    @Invoker
    boolean callHasOreVeins();

    @Invoker
    boolean callIsMobGenerationDisabled();
}
