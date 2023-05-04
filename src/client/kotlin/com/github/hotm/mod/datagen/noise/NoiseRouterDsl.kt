package com.github.hotm.mod.datagen.noise

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class NoiseRouterDsl(
    val barrierNoise: DensityFunctionDsl,
    val fluidLevelFloodNoise: DensityFunctionDsl,
    val fluidLevelSpreadNoise: DensityFunctionDsl,
    val lavaNoise: DensityFunctionDsl,
    val temperature: DensityFunctionDsl,
    val vegetation: DensityFunctionDsl,
    val continentalness: DensityFunctionDsl,
    val erosion: DensityFunctionDsl,
    val depth: DensityFunctionDsl,
    val weirdness: DensityFunctionDsl,
    val initialNonJaggedDensity: DensityFunctionDsl,
    val fullNoise: DensityFunctionDsl,
    val veinToggle: DensityFunctionDsl,
    val veinRidged: DensityFunctionDsl,
    val veinGap: DensityFunctionDsl
) {
    companion object {
        private fun field(
            name: String, getter: (NoiseRouterDsl) -> DensityFunctionDsl
        ): RecordCodecBuilder<NoiseRouterDsl, DensityFunctionDsl> =
            DensityFunctionDsl.CODEC.fieldOf(name).forGetter(getter)

        val CODEC: Codec<NoiseRouterDsl> = RecordCodecBuilder.create { instance ->
            instance.group(
                field("barrier", NoiseRouterDsl::barrierNoise),
                field("fluid_level_floodedness", NoiseRouterDsl::fluidLevelFloodNoise),
                field("fluid_level_spread", NoiseRouterDsl::fluidLevelSpreadNoise),
                field("lava", NoiseRouterDsl::lavaNoise),
                field("temperature", NoiseRouterDsl::temperature),
                field("vegetation", NoiseRouterDsl::vegetation),
                field("continents", NoiseRouterDsl::continentalness),
                field("erosion", NoiseRouterDsl::erosion),
                field("depth", NoiseRouterDsl::depth),
                field("ridges", NoiseRouterDsl::weirdness),
                field("initial_density_without_jaggedness", NoiseRouterDsl::initialNonJaggedDensity),
                field("final_density", NoiseRouterDsl::fullNoise),
                field("vein_toggle", NoiseRouterDsl::veinToggle),
                field("vein_ridged", NoiseRouterDsl::veinRidged),
                field("vein_gap", NoiseRouterDsl::veinGap)
            ).apply(instance, ::NoiseRouterDsl)
        }
    }
}

fun noiseRouter(
    barrierNoise: DensityFunctionDsl = zero,
    fluidLevelFloodNoise: DensityFunctionDsl = zero,
    fluidLevelSpreadNoise: DensityFunctionDsl = zero,
    lavaNoise: DensityFunctionDsl = zero,
    temperature: DensityFunctionDsl = zero,
    vegetation: DensityFunctionDsl = zero,
    continentalness: DensityFunctionDsl = zero,
    erosion: DensityFunctionDsl = zero,
    depth: DensityFunctionDsl = zero,
    weirdness: DensityFunctionDsl = zero,
    initialNonJaggedDensity: DensityFunctionDsl = zero,
    fullNoise: DensityFunctionDsl = zero,
    veinToggle: DensityFunctionDsl = zero,
    veinRidged: DensityFunctionDsl = zero,
    veinGap: DensityFunctionDsl = zero
) = NoiseRouterDsl(
    barrierNoise,
    fluidLevelFloodNoise,
    fluidLevelSpreadNoise,
    lavaNoise,
    temperature,
    vegetation,
    continentalness,
    erosion,
    depth,
    weirdness,
    initialNonJaggedDensity,
    fullNoise,
    veinToggle,
    veinRidged,
    veinGap
)
