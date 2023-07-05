package com.github.hotm.mod.datagen.noise

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class NoiseRouterDsl(
    val barrier: DensityFunctionDsl,
    val fluidLevelFloodedness: DensityFunctionDsl,
    val fluidLevelSpread: DensityFunctionDsl,
    val lava: DensityFunctionDsl,
    val temperature: DensityFunctionDsl,
    val vegetation: DensityFunctionDsl,
    val continentalness: DensityFunctionDsl,
    val erosion: DensityFunctionDsl,
    val depth: DensityFunctionDsl,
    val weirdness: DensityFunctionDsl,
    val initialDensityWithoutJaggedness: DensityFunctionDsl,
    val finalDensity: DensityFunctionDsl,
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
                field("barrier", NoiseRouterDsl::barrier),
                field("fluid_level_floodedness", NoiseRouterDsl::fluidLevelFloodedness),
                field("fluid_level_spread", NoiseRouterDsl::fluidLevelSpread),
                field("lava", NoiseRouterDsl::lava),
                field("temperature", NoiseRouterDsl::temperature),
                field("vegetation", NoiseRouterDsl::vegetation),
                field("continents", NoiseRouterDsl::continentalness),
                field("erosion", NoiseRouterDsl::erosion),
                field("depth", NoiseRouterDsl::depth),
                field("ridges", NoiseRouterDsl::weirdness),
                field("initial_density_without_jaggedness", NoiseRouterDsl::initialDensityWithoutJaggedness),
                field("final_density", NoiseRouterDsl::finalDensity),
                field("vein_toggle", NoiseRouterDsl::veinToggle),
                field("vein_ridged", NoiseRouterDsl::veinRidged),
                field("vein_gap", NoiseRouterDsl::veinGap)
            ).apply(instance, ::NoiseRouterDsl)
        }

        fun builder() = Builder()
    }

    class Builder(
        var barrier: DensityFunctionDsl = zero,
        var fluidLevelFloodedness: DensityFunctionDsl = zero,
        var fluidLevelSpread: DensityFunctionDsl = zero,
        var lava: DensityFunctionDsl = zero,
        var temperature: DensityFunctionDsl = zero,
        var vegetation: DensityFunctionDsl = zero,
        var continentalness: DensityFunctionDsl = zero,
        var erosion: DensityFunctionDsl = zero,
        var depth: DensityFunctionDsl = zero,
        var weirdness: DensityFunctionDsl = zero,
        var initialDensityWithoutJaggedness: DensityFunctionDsl = zero,
        var finalDensity: DensityFunctionDsl = zero,
        var veinToggle: DensityFunctionDsl = zero,
        var veinRidged: DensityFunctionDsl = zero,
        var veinGap: DensityFunctionDsl = zero
    ) {
        fun build() = NoiseRouterDsl(
            barrier,
            fluidLevelFloodedness,
            fluidLevelSpread,
            lava,
            temperature,
            vegetation,
            continentalness,
            erosion,
            depth,
            weirdness,
            initialDensityWithoutJaggedness,
            finalDensity,
            veinToggle,
            veinRidged,
            veinGap
        )

        fun barrier(df: DensityFunctionDsl) {
            barrier = df
        }

        fun fluidLevelFloodedness(df: DensityFunctionDsl) {
            fluidLevelFloodedness = df
        }

        fun fluidLevelSpread(df: DensityFunctionDsl) {
            fluidLevelSpread = df
        }

        fun lava(df: DensityFunctionDsl) {
            lava = df
        }

        fun temperature(df: DensityFunctionDsl) {
            temperature = df
        }

        fun vegetation(df: DensityFunctionDsl) {
            vegetation = df
        }

        fun continentalness(df: DensityFunctionDsl) {
            continentalness = df
        }

        fun erosion(df: DensityFunctionDsl) {
            erosion = df
        }

        fun depth(df: DensityFunctionDsl) {
            depth = df
        }

        fun weirdness(df: DensityFunctionDsl) {
            weirdness = df
        }

        fun initialDensityWithoutJaggedness(df: DensityFunctionDsl) {
            initialDensityWithoutJaggedness = df
        }

        fun finalDensity(df: DensityFunctionDsl) {
            finalDensity = df
        }

        fun veinToggle(df: DensityFunctionDsl) {
            veinToggle = df
        }

        fun veinRidged(df: DensityFunctionDsl) {
            veinRidged = df
        }

        fun veinGap(df: DensityFunctionDsl) {
            veinGap = df
        }
    }
}

fun noiseRouter(
    barrier: DensityFunctionDsl = zero,
    fluidLevelFloodedness: DensityFunctionDsl = zero,
    fluidLevelSpread: DensityFunctionDsl = zero,
    lava: DensityFunctionDsl = zero,
    temperature: DensityFunctionDsl = zero,
    vegetation: DensityFunctionDsl = zero,
    continentalness: DensityFunctionDsl = zero,
    erosion: DensityFunctionDsl = zero,
    depth: DensityFunctionDsl = zero,
    weirdness: DensityFunctionDsl = zero,
    initialDensityWithoutJaggedness: DensityFunctionDsl = zero,
    finalDensity: DensityFunctionDsl = zero,
    veinToggle: DensityFunctionDsl = zero,
    veinRidged: DensityFunctionDsl = zero,
    veinGap: DensityFunctionDsl = zero
) = NoiseRouterDsl(
    barrier,
    fluidLevelFloodedness,
    fluidLevelSpread,
    lava,
    temperature,
    vegetation,
    continentalness,
    erosion,
    depth,
    weirdness,
    initialDensityWithoutJaggedness,
    finalDensity,
    veinToggle,
    veinRidged,
    veinGap
)
