package com.github.hotm.mod.datagen.noise

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.world.biome.source.util.MultiNoiseUtil
import net.minecraft.world.gen.chunk.GenerationShapeConfig
import net.minecraft.world.gen.surfacebuilder.SurfaceRules

data class ChunkGeneratorSettingsDsl(
    val noise: GenerationShapeConfig,
    val defaultBlock: BlockState,
    val defaultFluid: BlockState,
    val noiseRouter: NoiseRouterDsl,
    val surfaceRule: SurfaceRules.MaterialRule,
    val spawnTarget: List<MultiNoiseUtil.NoiseHypercube>,
    val seaLevel: Int,
    val disableMobGeneration: Boolean,
    val aquifersEnabled: Boolean,
    val oreVeinsEnabled: Boolean,
    val legacyRandomSource: Boolean
) {
    companion object {
        val CODEC: Codec<ChunkGeneratorSettingsDsl> = RecordCodecBuilder.create { instance ->
            instance.group(
                GenerationShapeConfig.CODEC.fieldOf("noise")
                    .forGetter(ChunkGeneratorSettingsDsl::noise),
                BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettingsDsl::defaultBlock),
                BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettingsDsl::defaultFluid),
                NoiseRouterDsl.CODEC.fieldOf("noise_router").forGetter(ChunkGeneratorSettingsDsl::noiseRouter),
                SurfaceRules.MaterialRule.CODEC.fieldOf("surface_rule")
                    .forGetter(ChunkGeneratorSettingsDsl::surfaceRule),
                MultiNoiseUtil.NoiseHypercube.CODEC.listOf().fieldOf("spawn_target")
                    .forGetter(ChunkGeneratorSettingsDsl::spawnTarget),
                Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettingsDsl::seaLevel),
                Codec.BOOL.fieldOf("disable_mob_generation")
                    .forGetter(ChunkGeneratorSettingsDsl::disableMobGeneration),
                Codec.BOOL.fieldOf("aquifers_enabled").forGetter(ChunkGeneratorSettingsDsl::aquifersEnabled),
                Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(ChunkGeneratorSettingsDsl::oreVeinsEnabled),
                Codec.BOOL.fieldOf("legacy_random_source")
                    .forGetter(ChunkGeneratorSettingsDsl::legacyRandomSource)
            ).apply(instance, ::ChunkGeneratorSettingsDsl)
        }

        fun builder() = Builder()
    }

    class Builder(
        var noise: GenerationShapeConfig = GenerationShapeConfig(-64, 384, 1, 2),
        var defaultBlock: BlockState = Blocks.STONE.defaultState,
        var defaultFluid: BlockState = Blocks.WATER.defaultState,
        var noiseRouter: NoiseRouterDsl = noiseRouter(),
        var surfaceRule: SurfaceRules.MaterialRule = SurfaceRules.block(Blocks.STONE.defaultState),
        var spawnTarget: List<MultiNoiseUtil.NoiseHypercube> = listOf(),
        var seaLevel: Int = 64,
        var disableMobGeneration: Boolean = false,
        var aquifersEnabled: Boolean = true,
        var oreVeinsEnabled: Boolean = true,
        var legacyRandomSource: Boolean = false
    ) {
        fun build() = ChunkGeneratorSettingsDsl(
            noise,
            defaultBlock,
            defaultFluid,
            noiseRouter,
            surfaceRule,
            spawnTarget,
            seaLevel,
            disableMobGeneration,
            aquifersEnabled,
            oreVeinsEnabled,
            legacyRandomSource
        )

        fun noise(noise: GenerationShapeConfig) {
            this.noise = noise
        }

        fun noise(minY: Int, height: Int, noiseSizeHorizontal: Int, noiseSizeVertical: Int) {
            noise = GenerationShapeConfig(minY, height, noiseSizeHorizontal, noiseSizeVertical)
        }

        fun defaultBlock(state: BlockState) {
            defaultBlock = state
        }

        fun defaultFluid(state: BlockState) {
            defaultFluid = state
        }

        fun noiseRouter(prop: NoiseRouterDsl) {
            noiseRouter = prop
        }

        fun noiseRouter(configure: NoiseRouterDsl.Builder.() -> Unit) {
            noiseRouter = NoiseRouterDsl.builder().apply(configure).build()
        }

        fun surfaceRule(prop: SurfaceRules.MaterialRule) {
            surfaceRule = prop
        }

        fun surfaceRule(configure: SequenceBuilder.() -> Unit) {
            surfaceRule = SequenceBuilder().apply(configure).build()
        }

        fun spawnTargets(prop: List<MultiNoiseUtil.NoiseHypercube>) {
            spawnTarget = prop
        }

        fun seaLevel(prop: Int) {
            seaLevel = prop
        }

        fun disableMobGeneration(prop: Boolean) {
            disableMobGeneration = prop
        }

        fun aquifersEnabled(prop: Boolean) {
            aquifersEnabled = prop
        }

        fun oreVeinsEnabled(prop: Boolean) {
            oreVeinsEnabled = prop
        }

        fun legacyRandomSource(prop: Boolean) {
            legacyRandomSource = prop
        }
    }
}

fun chunkGeneratorSettings(
    noise: GenerationShapeConfig = GenerationShapeConfig(-64, 384, 1, 2),
    defaultBlock: BlockState = Blocks.STONE.defaultState,
    defaultFluid: BlockState = Blocks.WATER.defaultState,
    noiseRouter: NoiseRouterDsl = noiseRouter(),
    surfaceRule: SurfaceRules.MaterialRule = SurfaceRules.sequence(),
    spawnTarget: List<MultiNoiseUtil.NoiseHypercube> = listOf(),
    seaLevel: Int = 64,
    disableMobGeneration: Boolean = false,
    aquifersEnabled: Boolean = true,
    oreVeinsEnabled: Boolean = true,
    legacyRandomSource: Boolean = false
) = ChunkGeneratorSettingsDsl(
    noise,
    defaultBlock,
    defaultFluid,
    noiseRouter,
    surfaceRule,
    spawnTarget,
    seaLevel,
    disableMobGeneration,
    aquifersEnabled,
    oreVeinsEnabled,
    legacyRandomSource
)
