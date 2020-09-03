package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.gen.feature.decorator.CountChanceInRangeDecoratorConfig
import com.github.hotm.gen.feature.decorator.CountHeightmapInRangeDecoratorConfig
import com.github.hotm.gen.feature.segment.*
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.*

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
     * Configured plassein surface growth.
     */
    val CONFIGURED_PLASSEIN_SURFACE_GROWTH = register(
        "plassein_surface_growth", SEGMENTED_FEATURE.configure(
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
            .decorate(
                HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                    CountChanceInRangeDecoratorConfig(100, 200, 24, 0.5f)
                )
            )
    )

    /**
     * Refuse pile feature. This is similar to the vanilla FOREST_ROCK feature but generates in more places.
     */
    val REFUSE_PILE = register("refuse_pile", RefusePileFeature(PileFeatureConfig.CODEC))

    /**
     * Configured refuse pile feature.
     */
    val CONFIGURED_REFUSE_PILE = register(
        "refuse_pile", REFUSE_PILE.configure(PileFeatureConfig(THINKING_STONE, 0))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(2)
    )

    /**
     * Creates a plassein "tree" growth.
     */
    val PLASSEIN_GROWTH = register("plassein_growth", PlasseinGrowthFeature(PlasseinGrowthConfig.CODEC))

    /**
     * Configured plassein growth.
     */
    val CONFIGURED_PLASSEIN_GROWTH = register(
        "plassein_growth",
        PLASSEIN_GROWTH.configure(PlasseinGrowthConfig(PLASSEIN_STEM, PLASSEIN_BLOOM, 10, 10, 0.5, 0.5))
            .decorate(
                HotMDecorators.COUNT_HEIGHTMAP_IN_RANGE.configure(
                    CountHeightmapInRangeDecoratorConfig(16, 100, 4)
                )
            )
    )

    /**
     * Creates crystal structures originating from walls and ceilings.
     */
    val CRYSTAL_GROWTH = register("crystal_growth", CrystalGrowthFeature(CrystalGrowthConfig.CODEC))

    /**
     * Configured crystal growth.
     */
    val CONFIGURED_CRYSTAL_GROWTH = register(
        "crystal_growth", Feature.RANDOM_SELECTOR.configure(
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
        ).decorate(Decorator.RANGE.configure(RangeDecoratorConfig(15, 8, 128)).spreadHorizontally().repeat(40))
    )

    /**
     * Creates server tower like structures.
     */
    val SERVER_TOWER = register("server_tower", ServerTowerFeature(ServerTowerConfig.CODEC))

    /**
     * Configured server tower.
     */
    val CONFIGURED_SERVER_TOWER = register(
        "server_tower", Feature.RANDOM_SELECTOR.configure(
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
        ).decorate(
            HotMDecorators.COUNT_CHANCE_SURFACE_IN_RANGE.configure(
                CountChanceInRangeDecoratorConfig(8, 80, 2, 0.125f)
            )
        )
    )

    /**
     * Creates metal towers with blinking lights.
     */
    val TRANSMISSION_TOWER = register("transmission_tower", TransmissionTowerFeature(TransmissionTowerConfig.CODEC))

    /**
     * Configured transmission tower.
     */
    val CONFIGURED_TRANSMISSION_TOWER = register(
        "transmission_tower", Feature.RANDOM_SELECTOR.configure(
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
        ).decorate(
            HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                CountChanceInRangeDecoratorConfig(128, 192, 2, 0.0625f)
            )
        )
    )

    /**
     * Nectere portal feature. (Only intended to be used in dimensions that are not the Nectere dimension.)
     */
    val NON_NECTERE_SIDE_NECTERE_PORTAL =
        register("nns_nectere_portal", NecterePortalFeature(DefaultFeatureConfig.CODEC))

    /**
     * Configured Nectere portal feature.
     */
    val CONFIGURED_NON_NECTERE_SIDE_NECTERE_PORTAL =
        register("nns_nectere_portal", NON_NECTERE_SIDE_NECTERE_PORTAL.configure(FeatureConfig.DEFAULT))

    /**
     * Adds refuse piles similar to the mossy rocks in Giant Spruce Taigas.
     */
    fun addRefusePiles(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, CONFIGURED_REFUSE_PILE)
    }

    /**
     * Adds Plassein growths.
     */
    fun addPlasseinGrowths(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, CONFIGURED_PLASSEIN_GROWTH)
    }

    /**
     * Adds Plassein tree things.
     */
    fun addPlasseinSurfaceTrees(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, CONFIGURED_PLASSEIN_SURFACE_GROWTH)
    }

    /**
     * Adds crystal growths.
     */
    fun addCrystalGrowths(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, CONFIGURED_CRYSTAL_GROWTH)
    }

    /**
     * Adds server towers.
     */
    fun addServerTowers(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, CONFIGURED_SERVER_TOWER)
    }

    /**
     * Adds transmission towers.
     */
    fun addTransmissionTowers(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, CONFIGURED_TRANSMISSION_TOWER)
    }

    /**
     * Adds Nectere portals.
     */
    private fun addNecterePortals(genSettings: GenerationSettings.Builder) {
        genSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, CONFIGURED_NON_NECTERE_SIDE_NECTERE_PORTAL)
    }

    /**
     * Called to add Nectere portal potential to every biome.
     */
    fun addUbiquitousFeatures(settings: GenerationSettings.Builder) {
        // TODO: Investigate better ways to do this.
        addNecterePortals(settings)
    }

    /**
     * Register our structures with the existing biomes.
     */
    fun register() {
        HotMStructureFeatures.register()
    }

    /**
     * Used for statically registering features.
     */
    private fun <FC : FeatureConfig, F : Feature<FC>> register(name: String, feature: F): F {
        return Registry.register(Registry.FEATURE, HotMConstants.identifier(name), feature)
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