package com.github.hotm.mod.datagen.noise

import com.github.hotm.mod.util.BlockStateBuilder
import com.github.hotm.mod.util.blockState
import com.github.hotm.mod.world.gen.surfacebuilder.DensityThresholdCondition
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.VerticalSurfaceType
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler.NoiseParameters
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.DensityFunction
import net.minecraft.world.gen.DensityFunctions
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.surfacebuilder.SurfaceRules
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.MaterialCondition
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.MaterialRule

interface MaterialRuleBuilder {
    fun build(): MaterialRule
}

interface MaterialRuleParentBuilder {
    fun addChild(rule: MaterialRule)

    fun sequence(configure: SequenceBuilder.() -> Unit) {
        addChild(SequenceBuilder().apply(configure).build())
    }

    fun conditional(configure: ConditionalBuilder.() -> Unit) {
        addChild(ConditionalBuilder().apply(configure).build())
    }

    fun block(state: BlockState) {
        addChild(SurfaceRules.block(state))
    }

    fun block(block: Block) {
        block(block.defaultState)
    }

    fun block(block: Block, configure: BlockStateBuilder.() -> Unit) {
        block(blockState(block, configure))
    }

    fun stoneDepthBlock(
        offset: Int = 0, addSurfaceDepth: Boolean = false, secondaryDepthRange: Int = 0,
        surfaceType: VerticalSurfaceType = VerticalSurfaceType.FLOOR, block: Block
    ) {
        conditional {
            stoneDepth(offset, addSurfaceDepth, secondaryDepthRange, surfaceType)
            block(block)
        }
    }

    fun stoneDepthBlock(
        offset: Int = 0, addSurfaceDepth: Boolean = false, secondaryDepthRange: Int = 0,
        surfaceType: VerticalSurfaceType = VerticalSurfaceType.FLOOR, state: BlockState
    ) {
        conditional {
            stoneDepth(offset, addSurfaceDepth, secondaryDepthRange, surfaceType)
            block(state)
        }
    }
}

class SequenceBuilder : MaterialRuleBuilder, MaterialRuleParentBuilder {
    private val rules = mutableListOf<MaterialRule>()

    override fun build(): MaterialRule = SurfaceRules.sequence(*rules.toTypedArray())

    override fun addChild(rule: MaterialRule) {
        rules.add(rule)
    }
}

fun interface ConditionBuilder {
    fun condition(condition: MaterialCondition)

    fun biome(vararg biome: RegistryKey<Biome>) {
        condition(SurfaceRules.biome(*biome))
    }

    fun biome(vararg biome: Identifier) {
        condition(SurfaceRules.biome(*biome.map { RegistryKey.of(RegistryKeys.BIOME, it) }.toTypedArray()))
    }

    fun biome(configure: BiomeConditionBuilder.() -> Unit) {
        condition(BiomeConditionBuilder().apply(configure).build())
    }

    fun noiseThreshold(noise: RegistryKey<NoiseParameters>, min: Double, max: Double = Double.MAX_VALUE) {
        condition(SurfaceRules.noiseThreshold(noise, min, max))
    }

    fun noiseThreshold(noise: Identifier, min: Double, max: Double = Double.MAX_VALUE) {
        noiseThreshold(RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, noise), min, max)
    }

    fun densityThreshold(df: DensityFunction, min: Double, max: Double = Double.MAX_VALUE) {
        condition(DensityThresholdCondition(df, min, max))
    }

    fun densityThreshold(df: Holder<DensityFunction>, min: Double, max: Double = Double.MAX_VALUE) {
        densityThreshold(DensityFunctions.HolderHolder(df), min, max)
    }

    fun densityThreshold(df: RegistryKey<DensityFunction>, min: Double, max: Double = Double.MAX_VALUE) {
        densityThreshold(Holder.Reference.create(KeyHolderOwner.get(RegistryKeys.DENSITY_FUNCTION), df), min, max)
    }

    fun densityThreshold(df: Identifier, min: Double, max: Double = Double.MAX_VALUE) {
        densityThreshold(RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, df), min, max)
    }

    fun stoneDepth(
        offset: Int = 0, addSurfaceDepth: Boolean = false, secondaryDepthRange: Int = 0,
        surfaceType: VerticalSurfaceType = VerticalSurfaceType.FLOOR
    ) {
        condition(SurfaceRules.stoneDepth(offset, addSurfaceDepth, secondaryDepthRange, surfaceType))
    }

    val not: ConditionBuilder
        get() = ConditionBuilder { condition(SurfaceRules.not(it)) }

    fun verticalGradient(randomName: String, trueAtAndBelow: YOffset, falseAtAndAbove: YOffset) {
        condition(SurfaceRules.verticalGradient(randomName, trueAtAndBelow, falseAtAndAbove))
    }

    fun water(offset: Int = 0, surfaceDepthMultiplier: Int = 0) {
        condition(SurfaceRules.water(offset, surfaceDepthMultiplier))
    }
}

class ConditionalBuilder : MaterialRuleBuilder, MaterialRuleParentBuilder, ConditionBuilder {
    private var condition: MaterialCondition? = null
    private var thenRun: MaterialRule? = null

    override fun condition(condition: MaterialCondition) {
        this.condition = condition
    }

    override fun build(): MaterialRule {
        val condition = condition ?: throw IllegalStateException("No condition has been specified")
        val thenRun = thenRun ?: throw IllegalStateException("Nothing has been specified for this conditional to run")
        return SurfaceRules.condition(condition, thenRun)
    }

    override fun addChild(rule: MaterialRule) {
        if (thenRun != null) throw IllegalStateException("Conditionals only support one child")
        thenRun = rule
    }
}

class BiomeConditionBuilder {
    private val biomes = mutableListOf<RegistryKey<Biome>>()

    fun build() = SurfaceRules.biome(*biomes.toTypedArray())

    fun add(biome: RegistryKey<Biome>) {
        biomes.add(biome)
    }

    fun add(biome: Identifier) {
        biomes.add(RegistryKey.of(RegistryKeys.BIOME, biome))
    }
}
