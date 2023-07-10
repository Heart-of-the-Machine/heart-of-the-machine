package com.github.hotm.mod.world.gen.surfacebuilder

import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.RandomState

class NoiseApplier(private val randomState: RandomState) : DensityFunction.Visitor {
    override fun apply(densityFunction: DensityFunction): DensityFunction {
        return densityFunction
    }

    override fun visitNoise(noiseHolder: DensityFunction.NoiseHolder): DensityFunction.NoiseHolder {
        val holder = noiseHolder.noiseData
        val sampler = randomState.getOrCreateNoiseSampler(holder.key.orElseThrow())
        return DensityFunction.NoiseHolder(holder, sampler)
    }
}
