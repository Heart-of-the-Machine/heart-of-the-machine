package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.CountDecoratorConfig
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.feature.Feature

/**
 * Features for Heart of the Machine biomes.
 */
object HotMBiomeFeatures {
    private val THINKING_STONE = HotMBlocks.THINKING_STONE.defaultState

    /**
     * Refuse pile feature. This is similar to the vanilla FOREST_ROCK feature but generates in more places.
     */
    val REFUSE_PILE = register("refuse_pile", RefusePileFeature(PileFeatureConfig.CODEC))

    /**
     * Adds refuse piles similar to the mossy rocks in Giant Spruce Taigas.
     */
    fun addRefusePiles(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.LOCAL_MODIFICATIONS, REFUSE_PILE.configure(
                PileFeatureConfig(
                    THINKING_STONE, 0
                )
            ).createDecoratedFeature(Decorator.FOREST_ROCK.configure(CountDecoratorConfig(3)))
        )
    }

    /**
     * Used for statically registering a features.
     */
    private fun <F : Feature<*>> register(name: String, feature: F): F {
        return Registry.register(Registry.FEATURE, Identifier(HotMConstants.MOD_ID, name), feature)
    }
}