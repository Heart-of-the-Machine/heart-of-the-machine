package com.github.hotm.mod.world.gen.surfacebuilder

import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.RandomState

class NoiseApplier(private val randomState: RandomState) : DensityFunction.Visitor {
    override fun apply(densityFunction: DensityFunction): DensityFunction {
        return densityFunction
    }

    override fun method_42358(noiseHolder: DensityFunction.C_jnszknql): DensityFunction.C_jnszknql {
        val holder = noiseHolder.noiseData
        val sampler = randomState.getOrCreateNoiseSampler(holder.key.orElseThrow())
        return DensityFunction.C_jnszknql(holder, sampler)
    }
}
