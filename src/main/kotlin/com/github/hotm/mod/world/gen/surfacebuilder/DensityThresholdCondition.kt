package com.github.hotm.mod.world.gen.surfacebuilder

import com.github.hotm.mod.mixin.api.HotMMixinHelper
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.dynamic.CodecHolder
import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.surfacebuilder.SurfaceRules
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.MaterialCondition

data class DensityThresholdCondition(
    val density: DensityFunction, val minThreshold: Double, val maxThreshold: Double
) : MaterialCondition {
    companion object {
        val CODEC: CodecHolder<DensityThresholdCondition> = CodecHolder.create(RecordCodecBuilder.create { instance ->
            instance.group(
                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("density").forGetter(DensityThresholdCondition::density),
                Codec.DOUBLE.fieldOf("min_threshold").forGetter(DensityThresholdCondition::minThreshold),
                Codec.DOUBLE.fieldOf("max_threshold").forGetter(DensityThresholdCondition::maxThreshold)
            ).apply(instance, ::DensityThresholdCondition)
        })
    }

    override fun codec(): CodecHolder<out MaterialCondition> = CODEC

    override fun apply(ctx: SurfaceRules.Context): SurfaceRules.Condition {
        val newDensity = density.mapAll(NoiseApplier(HotMMixinHelper.getRandomState(ctx)))
        val funcCtx = ConditionFunctionContext(ctx)

        return object : SurfaceRules.LazyVerticalCondition(ctx) {
            override fun compute(): Boolean {
                val d = newDensity.compute(funcCtx)
                return d in minThreshold..maxThreshold
            }
        }
    }

    private class ConditionFunctionContext(private val ctx: SurfaceRules.Context) : DensityFunction.FunctionContext {
        override fun blockX(): Int {
            return HotMMixinHelper.getX(ctx)
        }

        override fun blockY(): Int {
            return HotMMixinHelper.getY(ctx)
        }

        override fun blockZ(): Int {
            return HotMMixinHelper.getZ(ctx)
        }

        override fun getBlender(): Blender {
            return HotMMixinHelper.getChunkNoiseSampler(ctx).blender
        }
    }
}
