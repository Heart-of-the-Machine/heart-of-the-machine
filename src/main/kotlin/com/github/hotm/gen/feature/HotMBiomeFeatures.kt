package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.gen.feature.decorator.CountChanceInRangeDecoratorConfig
import com.github.hotm.gen.feature.decorator.CountHeightmapInRangeDecoratorConfig
import com.github.hotm.gen.feature.segment.*
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.CountDecoratorConfig
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.RandomFeatureConfig

/**
 * Features for Heart of the Machine biomes.
 */
object HotMBiomeFeatures {
    private val THINKING_STONE = HotMBlocks.THINKING_STONE.defaultState
    private val PLASSEIN_STEM = HotMBlocks.PLASSEIN_STEM.defaultState
    private val PLASSEIN_BLOOM = HotMBlocks.PLASSEIN_BLOOM.defaultState

    /**
     * Creates a segmented feature such as a plassein tree or other plant.
     */
    val SEGMENTED_FEATURE = register("segmented_feature", SegmentedFeature(SegmentedFeatureConfig.CODEC))

    /**
     * Refuse pile feature. This is similar to the vanilla FOREST_ROCK feature but generates in more places.
     */
    val REFUSE_PILE = register("refuse_pile", RefusePileFeature(PileFeatureConfig.CODEC))

    /**
     * Creates a plassein "tree" growth.
     */
    val PLASSEIN_GROWTH = register("plassein_growth", PlasseinGrowthFeature(PlasseinGrowthConfig.CODEC))

    /**
     * Creates crystal structures originating from walls and ceilings.
     */
    val CRYSTAL_GROWTH = register("crystal_growth", CrystalGrowthFeature(CrystalGrowthConfig.CODEC))

    /**
     * Creates server tower like structures.
     */
    val SERVER_TOWER = register("server_tower", ServerTowerFeature(ServerTowerConfig.CODEC))

    /**
     * Creates metal towers with blinking lights.
     */
    val TRANSMISSION_TOWER = register("transmission_tower", TransmissionTowerFeature(TransmissionTowerConfig.CODEC))

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
                    HotMDecorators.COUNT_HEIGHTMAP_IN_RANGE.configure(
                        CountHeightmapInRangeDecoratorConfig(16, 100, 4)
                    )
                )
        )
    }

    /**
     * Adds Plassein tree things.
     */
    fun addPlasseinSurfaceTrees(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.VEGETAL_DECORATION,
            SEGMENTED_FEATURE.configure(
                SegmentedFeatureConfig(
                    PlasseinStemSegment(
                        PLASSEIN_STEM,
                        8,
                        8,
                        PlasseinBranchSegment(PLASSEIN_STEM, 2, 2, PlasseinLeafSegment(PLASSEIN_BLOOM, 2, 2, 1, 1)),
                        PlasseinLeafSegment(PLASSEIN_BLOOM, 4, 3, 2, 2)
                    )
                )
            )
                .createDecoratedFeature(
                    HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                        CountChanceInRangeDecoratorConfig(100, 200, 24, 0.5f)
                    )
                )
        )
    }

    /**
     * Adds crystal growths.
     */
    fun addCrystalGrowths(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.UNDERGROUND_DECORATION,
            Feature.RANDOM_SELECTOR.configure(
                RandomFeatureConfig(
                    listOf(
                        CRYSTAL_GROWTH.configure(
                            CrystalGrowthConfig(
                                listOf(HotMBlocks.THINKING_STONE),
                                HotMBlocks.CYAN_CRYSTAL.defaultState,
                                5,
                                0.5f
                            )
                        ).withChance(0.5f)
                    ),
                    CRYSTAL_GROWTH.configure(
                        CrystalGrowthConfig(
                            listOf(HotMBlocks.THINKING_STONE),
                            HotMBlocks.MAGENTA_CRYSTAL.defaultState,
                            5,
                            0.5f
                        )
                    )
                )
            ).createDecoratedFeature(Decorator.COUNT_RANGE.configure(RangeDecoratorConfig(15, 0, 0, 100)))
        )
    }

    /**
     * Adds server towers.
     */
    fun addServerTowers(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.UNDERGROUND_DECORATION,
            Feature.RANDOM_SELECTOR.configure(
                RandomFeatureConfig(
                    listOf(
                        SERVER_TOWER.configure(
                            ServerTowerConfig(
                                1,
                                5,
                                1,
                                10,
                                0,
                                2,
                                5,
                                0.5f,
                                HotMBlocks.SMOOTH_THINKING_STONE.defaultState,
                                HotMBlocks.CYAN_CRYSTAL_LAMP.defaultState,
                                HotMBlocks.CYAN_THINKING_STONE_LAMP.defaultState
                            )
                        ).withChance(0.5f)
                    ),
                    SERVER_TOWER.configure(
                        ServerTowerConfig(
                            1,
                            5,
                            1,
                            10,
                            0,
                            2,
                            5,
                            0.5f,
                            HotMBlocks.SMOOTH_THINKING_STONE.defaultState,
                            HotMBlocks.MAGENTA_CRYSTAL_LAMP.defaultState,
                            HotMBlocks.MAGENTA_THINKING_STONE_LAMP.defaultState
                        )
                    )
                )
            ).createDecoratedFeature(
                HotMDecorators.COUNT_CHANCE_SURFACE_IN_RANGE.configure(
                    CountChanceInRangeDecoratorConfig(8, 80, 2, 0.125f)
                )
            )
        )
    }

    /**
     * Adds transmission towers.
     */
    fun addTransmissionTowers(biome: Biome) {
        biome.addFeature(
            GenerationStep.Feature.SURFACE_STRUCTURES,
            Feature.RANDOM_SELECTOR.configure(
                RandomFeatureConfig(
                    listOf(
                        TRANSMISSION_TOWER.configure(
                            TransmissionTowerConfig(
                                30,
                                60,
                                5,
                                15,
                                4,
                                0.125f,
                                0.125f,
                                HotMBlocks.MACHINE_CASING.defaultState,
                                HotMBlocks.PLASSEIN_MACHINE_CASING.defaultState,
                                PLASSEIN_BLOOM,
                                HotMBlocks.CYAN_MACHINE_CASING_LAMP.defaultState
                            )
                        ).withChance(0.5f)
                    ),
                    TRANSMISSION_TOWER.configure(
                        TransmissionTowerConfig(
                            30,
                            60,
                            5,
                            15,
                            4,
                            0.125f,
                            0.125f,
                            HotMBlocks.MACHINE_CASING.defaultState,
                            HotMBlocks.PLASSEIN_MACHINE_CASING.defaultState,
                            PLASSEIN_BLOOM,
                            HotMBlocks.MAGENTA_MACHINE_CASING_LAMP.defaultState
                        )
                    )
                )
            ).createDecoratedFeature(
                HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                    CountChanceInRangeDecoratorConfig(128, 192, 2, 0.0625f)
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
        return Registry.register(Registry.FEATURE, HotMConstants.identifier(name), feature)
    }
}