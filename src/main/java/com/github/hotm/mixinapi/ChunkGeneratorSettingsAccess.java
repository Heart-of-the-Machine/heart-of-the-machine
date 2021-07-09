package com.github.hotm.mixinapi;

import com.github.hotm.mixin.ChunkGeneratorSettingsAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class ChunkGeneratorSettingsAccess {
    public static ChunkGeneratorSettings create(StructuresConfig structuresConfig,
                                                                      GenerationShapeConfig generationShapeConfig,
                                                                      BlockState defaultBlock, BlockState defaultFluid,
                                                                      int bedrockCeilingY, int bedrockFloorY,
                                                                      int seaLevel, int minSurfaceLevel,
                                                                      boolean mobGenerationDisabled, boolean aquifers,
                                                                      boolean noiseCaves, boolean deepslate,
                                                                      boolean oreVeins, boolean noodleCaves) {
        return ChunkGeneratorSettingsAccessor
                .create(structuresConfig, generationShapeConfig, defaultBlock, defaultFluid, bedrockCeilingY,
                        bedrockFloorY, seaLevel, minSurfaceLevel, mobGenerationDisabled, aquifers, noiseCaves,
                        deepslate, oreVeins, noodleCaves);
    }

    public static boolean hasNoiseCaves(ChunkGeneratorSettings settings) {
        return ((ChunkGeneratorSettingsAccessor) (Object) settings).callHasNoiseCaves();
    }

    public static boolean hasAquifers(ChunkGeneratorSettings settings) {
        return ((ChunkGeneratorSettingsAccessor) (Object) settings).callHasAquifers();
    }

    public static boolean hasNoodleCaves(ChunkGeneratorSettings settings) {
        return ((ChunkGeneratorSettingsAccessor) (Object) settings).callHasNoodleCaves();
    }

    public static boolean hasOreVeins(ChunkGeneratorSettings settings) {
        return ((ChunkGeneratorSettingsAccessor) (Object) settings).callHasOreVeins();
    }

    public static boolean isMobGenerationDisabled(ChunkGeneratorSettings settings) {
        return ((ChunkGeneratorSettingsAccessor) (Object) settings).callIsMobGenerationDisabled();
    }
}
