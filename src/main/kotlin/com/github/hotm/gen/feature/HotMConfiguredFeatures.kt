package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.gen.feature.decorator.CountChanceInRangeDecoratorConfig
import com.github.hotm.gen.feature.decorator.CountHeightmapInRangeDecoratorConfig
import com.github.hotm.gen.feature.segment.PlasseinBranchSegment
import com.github.hotm.gen.feature.segment.PlasseinLeafSegment
import com.github.hotm.gen.feature.segment.PlasseinStemSegment
import com.github.hotm.gen.feature.segment.SegmentedFeatureConfig
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.*

object HotMConfiguredFeatures {
    private val THINKING_STONE = HotMBlocks.THINKING_STONE.defaultState
    private val PLASSEIN_BLOOM = HotMBlocks.PLASSEIN_BLOOM.defaultState
    private val PLASSEIN_LEAVES = HotMBlocks.PLASSEIN_LEAVES.defaultState
    private val PLASSEIN_LOG = HotMBlocks.PLASSEIN_LOG.defaultState
    private val PLASSEIN_STEM = HotMBlocks.PLASSEIN_STEM.defaultState

    /**
     * General plassein surface growth.
     */
    val PLASSEIN_SURFACE_GROWTH = register(
        "plassein_surface_growth", HotMFeatures.SEGMENTED_FEATURE.configure(
            SegmentedFeatureConfig(
                PlasseinStemSegment(
                    PLASSEIN_LOG,
                    8,
                    8,
                    PlasseinBranchSegment(
                        PLASSEIN_LOG,
                        2,
                        2,
                        PlasseinLeafSegment(PLASSEIN_LEAVES, 2, 2, 1, 1)
                    ),
                    PlasseinLeafSegment(PLASSEIN_LEAVES, 4, 3, 2, 2)
                )
            )
        )
    )

    /**
     * Plassein surface growth configured for generation in a forest.
     */
    val FOREST_SURFACE_GROWTHS = register(
        "forest_surface_growths", PLASSEIN_SURFACE_GROWTH.decorate(
            HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                CountChanceInRangeDecoratorConfig(100, 200, 24, 0.5f)
            )
        )
    )

    /**
     * Configured refuse pile feature.
     */
    val REFUSE_PILE = register(
        "refuse_pile", HotMFeatures.REFUSE_PILE.configure(PileFeatureConfig(THINKING_STONE, 0))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(2)
    )

    /**
     * Configured plassein growth.
     */
    val PLASSEIN_GROWTH = register(
        "plassein_growth",
        HotMFeatures.PLASSEIN_GROWTH.configure(
            PlasseinGrowthConfig(
                PLASSEIN_STEM,
                PLASSEIN_BLOOM,
                10,
                10,
                0.5,
                0.5
            )
        )
            .decorate(
                HotMDecorators.COUNT_HEIGHTMAP_IN_RANGE.configure(
                    CountHeightmapInRangeDecoratorConfig(16, 100, 4)
                )
            )
    )

    /**
     * Configured crystal growth.
     */
    val CRYSTAL_GROWTH = register(
        "crystal_growth", Feature.RANDOM_SELECTOR.configure(
            RandomFeatureConfig(
                listOf(
                    HotMFeatures.CRYSTAL_GROWTH.configure(
                        CrystalGrowthConfig(
                            listOf(HotMBlocks.THINKING_STONE),
                            HotMBlocks.CYAN_CRYSTAL.defaultState,
                            5,
                            0.5f
                        )
                    ).withChance(0.5f)
                ),
                HotMFeatures.CRYSTAL_GROWTH.configure(
                    CrystalGrowthConfig(
                        listOf(HotMBlocks.THINKING_STONE),
                        HotMBlocks.MAGENTA_CRYSTAL.defaultState,
                        5,
                        0.5f
                    )
                )
            )
        ).decorate(Decorator.RANGE.configure(RangeDecoratorConfig(15, 8, 128)).spreadHorizontally().repeat(10))
    )

    /**
     * Creates leylines that stretch throughout a chunk, connecting to leylines in adjacent chunks.
     */
    val LEYLINES = register("leylines", HotMFeatures.LEYLINE.configure(FeatureConfig.DEFAULT))

    /**
     * Configured server tower.
     */
    val SERVER_TOWER = register(
        "server_tower", Feature.RANDOM_SELECTOR.configure(
            RandomFeatureConfig(
                listOf(
                    HotMFeatures.SERVER_TOWER.configure(
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
                HotMFeatures.SERVER_TOWER.configure(
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
        ).decorate(
            HotMDecorators.COUNT_CHANCE_SURFACE_IN_RANGE.configure(
                CountChanceInRangeDecoratorConfig(8, 80, 2, 0.125f)
            )
        )
    )

    /**
     * Configured transmission tower.
     */
    val TRANSMISSION_TOWER = register(
        "transmission_tower", Feature.RANDOM_SELECTOR.configure(
            RandomFeatureConfig(
                listOf(
                    HotMFeatures.TRANSMISSION_TOWER.configure(
                        TransmissionTowerConfig(
                            30,
                            60,
                            5,
                            15,
                            4,
                            0.25f,
                            0.5f,
                            HotMBlocks.METAL_BRACING.defaultState,
                            HotMBlocks.PLASSEIN_BRACING.defaultState,
                            PLASSEIN_LEAVES,
                            HotMBlocks.CYAN_MACHINE_CASING_LAMP.defaultState
                        )
                    ).withChance(0.5f)
                ),
                HotMFeatures.TRANSMISSION_TOWER.configure(
                    TransmissionTowerConfig(
                        30,
                        60,
                        5,
                        15,
                        4,
                        0.25f,
                        0.5f,
                        HotMBlocks.METAL_BRACING.defaultState,
                        HotMBlocks.PLASSEIN_BRACING.defaultState,
                        PLASSEIN_LEAVES,
                        HotMBlocks.MAGENTA_MACHINE_CASING_LAMP.defaultState
                    )
                )
            )
        ).decorate(
            HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                CountChanceInRangeDecoratorConfig(128, 192, 2, 0.0625f)
            )
        )
    )

    /**
     * Configured Nectere portal feature.
     */
    val NON_NECTERE_SIDE_NECTERE_PORTAL =
        register(
            "nns_nectere_portal",
            HotMFeatures.NON_NECTERE_SIDE_NECTERE_PORTAL.configure(FeatureConfig.DEFAULT)
        )

    /**
     * Adds refuse piles similar to the mossy rocks in Giant Spruce Taigas.
     */
    fun addRefusePiles(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, REFUSE_PILE)
    }

    /**
     * Adds Plassein growths.
     */
    fun addPlasseinGrowths(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, PLASSEIN_GROWTH)
    }

    /**
     * Adds Plassein tree things.
     */
    fun addPlasseinSurfaceTrees(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, FOREST_SURFACE_GROWTHS)
    }

    /**
     * Adds crystal growths.
     */
    fun addCrystalGrowths(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, CRYSTAL_GROWTH)
    }

    /**
     * Adds leylines.
     */
    fun addLeylines(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, LEYLINES)
    }

    /**
     * Adds server towers.
     */
    fun addServerTowers(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, SERVER_TOWER)
    }

    /**
     * Adds transmission towers.
     */
    fun addTransmissionTowers(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, TRANSMISSION_TOWER)
    }

    /**
     * Adds Nectere portals.
     */
    private fun addNecterePortals(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, NON_NECTERE_SIDE_NECTERE_PORTAL)
    }

    /**
     * Called to add Nectere portal potential to every biome.
     */
    fun addUbiquitousFeatures(settings: GenerationSettings.Builder) {
        // TODO: Investigate better ways to do this.
        addNecterePortals(settings)
    }

    /**
     * Used for statically registering configured features.
     */
    private fun <FC : FeatureConfig> register(
        name: String,
        config: ConfiguredFeature<FC, *>
    ): ConfiguredFeature<FC, *> {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, HotMConstants.identifier(name), config)
    }
}