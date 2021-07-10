package com.github.hotm.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

/**
 * Accesses StructureFeature's private fields and methods.
 */
@Mixin(StructureFeature.class)
public interface StructureFeatureAccessor {
    @Invoker
    boolean callShouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed,
                              ChunkRandom random, ChunkPos pos, Biome biome, ChunkPos chunkPos, FeatureConfig config,
                              HeightLimitView world);
}
