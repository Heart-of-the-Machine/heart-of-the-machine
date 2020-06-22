package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.CountDecoratorConfig
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature

/**
 * Features for Heart of the Machine biomes.
 */
object HotMBiomeFeatures {
    private val THINKING_STONE = HotMBlocks.THINKING_STONE.defaultState
    private val PLASSEIN_STEM = HotMBlocks.PLASSEIN_STEM.defaultState
    private val PLASSEIN_BLOOM = HotMBlocks.PLASSEIN_BLOOM.defaultState

    /**
     * Refuse pile feature. This is similar to the vanilla FOREST_ROCK feature but generates in more places.
     */
    val REFUSE_PILE = register("refuse_pile", RefusePileFeature(PileFeatureConfig.CODEC))

    /**
     * Creates a plassein "tree" growth.
     */
    val PLASSEIN_GROWTH = register("plassein_growth", PlasseinGrowthFeature(PlasseinGrowthConfig.CODEC))

    /**
     * Nectere portal structure feature.
     */
    val NECTERE_PORTAL = HotMStructureFeatures.NECTERE_PORTAL.configure(DefaultFeatureConfig.INSTANCE)

    /**
     * Adds refuse piles similar to the mossy rocks in Giant Spruce Taigas.
     */
    fun addRefusePiles(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.LOCAL_MODIFICATIONS,
            REFUSE_PILE.configure(PileFeatureConfig(THINKING_STONE, 0))
                .createDecoratedFeature(Decorator.FOREST_ROCK.configure(CountDecoratorConfig(3)))
        )
    }

    /**
     * Adds Plassein growths.
     */
    fun addPlasseinGrowths(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.VEGETAL_DECORATION,
            PLASSEIN_GROWTH.configure(PlasseinGrowthConfig(PLASSEIN_STEM, PLASSEIN_BLOOM, 10, 10, 0.5, 0.5))
                .createDecoratedFeature(
                    Decorator.COUNT_EXTRA_HEIGHTMAP.configure(
                        CountExtraChanceDecoratorConfig(
                            3,
                            0.1F,
                            1
                        )
                    )
                )
        )
    }

    /**
     * Register our structures with the existing biomes.
     */
    fun register() {
        HotMStructureFeatures.register()

        for (biome in Registry.BIOME) {
            if (biome.category != Biome.Category.OCEAN && biome.category != Biome.Category.RIVER && biome.category != Biome.Category.NETHER && biome.category != Biome.Category.THEEND) {
                biome.addStructureFeature(NECTERE_PORTAL)
            }
        }
    }

    /**
     * Used for statically registering a features.
     */
    private fun <F : Feature<*>> register(name: String, feature: F): F {
        return Registry.register(Registry.FEATURE, Identifier(HotMConstants.MOD_ID, name), feature)
    }
}