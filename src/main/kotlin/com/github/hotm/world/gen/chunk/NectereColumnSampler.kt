package com.github.hotm.world.gen.chunk

import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.noise.InterpolatedNoiseSampler
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler
import net.minecraft.util.math.noise.SimplexNoiseSampler
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.minecraft.world.gen.NoiseColumnSampler
import net.minecraft.world.gen.chunk.GenerationShapeConfig
import net.minecraft.world.gen.chunk.WeightSampler


/**
 * Samples noise values for use in chunk generation.
 *
 * This version is slightly different from the default noise column sampler in the way it applies biome noise.
 *
 * Note that this extends NoiseColumnSampler because I would have to reimplement more things if it didn't.
 */
class NectereColumnSampler(
    private val biomeSource: BiomeSource,
    private val horizontalNoiseResolution: Int,
    private val verticalNoiseResolution: Int,
    private val noiseSizeY: Int,
    private val config: GenerationShapeConfig,
    private val noise: InterpolatedNoiseSampler,
    private val islandNoise: SimplexNoiseSampler?,
    private val densityNoise: OctavePerlinNoiseSampler,
    private val weightSampler: WeightSampler
) : NoiseColumnSampler(
    biomeSource,
    horizontalNoiseResolution,
    verticalNoiseResolution,
    noiseSizeY,
    config,
    noise,
    islandNoise,
    densityNoise,
    weightSampler
) {
    private val topSlideTarget: Double = config.topSlide.target.toDouble()
    private val topSlideSize: Double = config.topSlide.size.toDouble()
    private val topSlideOffset: Double = config.topSlide.offset.toDouble()
    private val bottomSlideTarget: Double = config.bottomSlide.target.toDouble()
    private val bottomSlideSize: Double = config.bottomSlide.size.toDouble()
    private val bottomSlideOffset: Double = config.bottomSlide.offset.toDouble()
    private val densityFactor: Double = config.densityFactor
    private val densityOffset: Double = config.densityOffset

    /**
     * Samples the noise for the given column and stores it in the buffer parameter.
     */
    override fun sampleNoiseColumn(
        buffer: DoubleArray,
        x: Int,
        z: Int,
        config: GenerationShapeConfig,
        seaLevel: Int,
        minY: Int,
        noiseSizeY: Int
    ) {
        val biomeDepth: Double
        val biomeScale: Double

        if (islandNoise != null) {
            biomeDepth = (TheEndBiomeSource.getNoiseAt(islandNoise, x, z) - 8.0f).toDouble()
            biomeScale = if (biomeDepth > 0.0) {
                0.25
            } else {
                1.0
            }
        } else {
            var scaleSum = 0.0f
            var depthSum = 0.0f
            var weightSum = 0.0f
            val l = biomeSource.getBiomeForNoiseGen(x, seaLevel, z).depth

            for (m in -2..2) {
                for (n in -2..2) {
                    val biome = biomeSource.getBiomeForNoiseGen(x + m, seaLevel, z + n)
                    val blockDepth = biome.depth
                    val blockScale = biome.scale
                    var adjustedBlockDepth: Float
                    var adjustedBlockScale: Float

                    if (config.isAmplified && blockDepth > 0.0f) {
                        adjustedBlockDepth = 1.0f + blockDepth * 2.0f
                        adjustedBlockScale = 1.0f + blockScale * 4.0f
                    } else {
                        adjustedBlockDepth = blockDepth
                        adjustedBlockScale = blockScale
                    }

                    val u = if (blockDepth > l) 0.5f else 1.0f
                    val weight = u * BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (adjustedBlockDepth + 2.0f)
                    scaleSum += adjustedBlockScale * weight
                    depthSum += adjustedBlockDepth * weight
                    weightSum += weight
                }
            }

            val smoothedDepth = depthSum / weightSum
            val smoothedScale = scaleSum / weightSum
            val preBiomeDepth = (smoothedDepth * 0.5f - 0.125f).toDouble()
            val preBiomeScale = (smoothedScale * 0.9f + 0.1f).toDouble()
            biomeDepth = preBiomeDepth * 0.265625
            biomeScale = 96.0 / preBiomeScale
        }

        val xzScale = 684.412 * config.sampling.xzScale
        val yScale = 684.412 * config.sampling.yScale
        val xzFactor = xzScale / config.sampling.xzFactor
        val yFactor = yScale / config.sampling.yFactor
        val randomDensityOffset = if (config.hasRandomDensityOffset()) getDensityNoise(x, z) else 0.0

        for (baseSampleY in 0..noiseSizeY) {
            val sampleY = baseSampleY + minY
            var density = noise.sample(x, sampleY, z, xzScale, yScale, xzFactor, yFactor)

            density += getOffset(sampleY, biomeDepth, biomeScale, randomDensityOffset)
            density = weightSampler.sample(
                density,
                sampleY * verticalNoiseResolution,
                z * horizontalNoiseResolution,
                x * horizontalNoiseResolution
            )
            density = applySlides(density, sampleY)

            buffer[baseSampleY] = density
        }
    }

    /**
     * Calculates an offset for the noise.
     *
     * For example in the overworld, this makes lower y values solid while making higher y values air.
     */
    private fun getOffset(sampleY: Int, depth: Double, scale: Double, randomDensityOffset: Double): Double {
        val densityAdjustY = 1.0 - sampleY.toDouble() * 2.0 / SAMPLE_SIZE_Y + randomDensityOffset
        val preDensityAdjust = (densityAdjustY + depth) * scale
        val densityAdjust = preDensityAdjust * densityFactor + densityOffset
        return densityAdjust * (if (densityAdjust > 0.0) 4.0 else 1.0)
    }

    /**
     * Interpolates the noise at the top and bottom of the world.
     */
    private fun applySlides(noise: Double, sampleY: Int): Double {
        var noise = noise
        val minSampleY = MathHelper.floorDiv(config.minimumY, verticalNoiseResolution)
        val baseSampleY = sampleY - minSampleY

        if (topSlideSize > 0.0) {
            val e = ((noiseSizeY - baseSampleY).toDouble() - topSlideOffset) / topSlideSize
            noise = MathHelper.clampedLerp(topSlideTarget, noise, e)
        }
        if (bottomSlideSize > 0.0) {
            val e = (baseSampleY.toDouble() - bottomSlideOffset) / bottomSlideSize
            noise = MathHelper.clampedLerp(bottomSlideTarget, noise, e)
        }

        return noise
    }

    /**
     * Applies a random change to the density to subtly vary the height of the terrain.
     */
    private fun getDensityNoise(x: Int, z: Int): Double {
        val d = densityNoise.sample(
            (x * 200).toDouble(), 10.0,
            (z * 200).toDouble(), 1.0, 0.0, true
        )
        val f: Double
        f = if (d < 0.0) {
            -d * 0.3
        } else {
            d
        }
        val g = f * 24.575625 - 2.0
        return if (g < 0.0) g * 0.009486607142857142 else Math.min(g, 1.0) * 0.006640625
    }

    companion object {
        private const val SAMPLE_SIZE_Y = 32.0

        /**
         * Table of weights used to weight faraway biomes less than nearby biomes.
         */
        private val BIOME_WEIGHT_TABLE = Util.make(
            FloatArray(25)
        ) { array: FloatArray ->
            for (i in -2..2) {
                for (j in -2..2) {
                    val f = 10.0f / MathHelper.sqrt((i * i + j * j).toFloat() + 0.2f)
                    array[i + 2 + (j + 2) * 5] = f
                }
            }
        } as FloatArray
    }

}
