package com.github.hotm.mod.datagen.noise

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.world.biome.source.util.MultiNoiseUtil
import net.minecraft.world.gen.chunk.GenerationShapeConfig
import net.minecraft.world.gen.surfacebuilder.SurfaceRules

data class ChunkGeneratorSettingsDsl(
    val generationShapeConfig: GenerationShapeConfig,
    val defaultBlock: BlockState,
    val defaultFluid: BlockState,
    val noiseRouter: NoiseRouterDsl,
    val surfaceRule: SurfaceRules.MaterialRule,
    val spawnTarget: List<MultiNoiseUtil.NoiseHypercube>,
    val seaLevel: Int,
    val mobGenerationDisabled: Boolean,
    val aquifersEnabled: Boolean,
    val oreVeinsEnabled: Boolean,
    val useLegacyRandomGenerator: Boolean
) {
    companion object {
        val CODEC: Codec<ChunkGeneratorSettingsDsl> = RecordCodecBuilder.create { instance ->
            instance.group(
                GenerationShapeConfig.CODEC.fieldOf("noise")
                    .forGetter(ChunkGeneratorSettingsDsl::generationShapeConfig),
                BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettingsDsl::defaultBlock),
                BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettingsDsl::defaultFluid),
                NoiseRouterDsl.CODEC.fieldOf("noise_router").forGetter(ChunkGeneratorSettingsDsl::noiseRouter),
                SurfaceRules.MaterialRule.CODEC.fieldOf("surface_rule")
                    .forGetter(ChunkGeneratorSettingsDsl::surfaceRule),
                MultiNoiseUtil.NoiseHypercube.CODEC.listOf().fieldOf("spawn_target")
                    .forGetter(ChunkGeneratorSettingsDsl::spawnTarget),
                Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettingsDsl::seaLevel),
                Codec.BOOL.fieldOf("disable_mob_generation")
                    .forGetter(ChunkGeneratorSettingsDsl::mobGenerationDisabled),
                Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettingsDsl::aquifersEnabled),
                Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettingsDsl::oreVeinsEnabled),
                Codec.BOOL.fieldOf("legacy_random_source")
                    .forGetter(ChunkGeneratorSettingsDsl::useLegacyRandomGenerator)
            ).apply(instance, ::ChunkGeneratorSettingsDsl)
        }
    }
}

fun chunkGeneratorSettings(
    generationShapeConfig: GenerationShapeConfig = GenerationShapeConfig(-64, 384, 1, 2),
    defaultBlock: BlockState = Blocks.STONE.defaultState,
    defaultFluid: BlockState = Blocks.WATER.defaultState,
    noiseRouter: NoiseRouterDsl = noiseRouter(),
    surfaceRule: SurfaceRules.MaterialRule = SurfaceRules.sequence(),
    spawnTarget: List<MultiNoiseUtil.NoiseHypercube> = listOf(),
    seaLevel: Int = 64,
    mobGenerationDisabled: Boolean = false,
    aquifersEnabled: Boolean = true,
    oreVeinsEnabled: Boolean = true,
    useLegacyRandomGenerator: Boolean = false
) = ChunkGeneratorSettingsDsl(
    generationShapeConfig,
    defaultBlock,
    defaultFluid,
    noiseRouter,
    surfaceRule,
    spawnTarget,
    seaLevel,
    mobGenerationDisabled,
    aquifersEnabled,
    oreVeinsEnabled,
    useLegacyRandomGenerator
)
