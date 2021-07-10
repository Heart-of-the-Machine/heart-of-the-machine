package com.github.hotm.world.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.mixinapi.FeatureAccess
import com.github.hotm.world.gen.feature.decorator.CountChanceInRangeDecoratorConfig
import com.github.hotm.world.gen.feature.decorator.CountHeightmapInRangeDecoratorConfig
import com.github.hotm.world.gen.feature.segment.PlasseinBranchSegment
import com.github.hotm.world.gen.feature.segment.PlasseinLeafSegment
import com.github.hotm.world.gen.feature.segment.PlasseinStemSegment
import com.github.hotm.world.gen.feature.segment.SegmentedFeatureConfig
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.feature.*

object HotMConfiguredFeatures {
    private val THINKING_STONE by lazy { HotMBlocks.THINKING_STONE.defaultState }
    private val PLASSEIN_BLOOM by lazy { HotMBlocks.PLASSEIN_BLOOM.defaultState }
    private val PLASSEIN_LEAVES by lazy { HotMBlocks.PLASSEIN_LEAVES.defaultState }
    private val PLASSEIN_LOG by lazy { HotMBlocks.PLASSEIN_LOG.defaultState }
    private val PLASSEIN_STEM by lazy { HotMBlocks.PLASSEIN_STEM.defaultState }

    /**
     * General plassein surface growth.
     */
    lateinit var PLASSEIN_SURFACE_GROWTH: ConfiguredFeature<SegmentedFeatureConfig, *>
        private set

    /**
     * Plassein surface growth configured for generation in a forest.
     */
    lateinit var FOREST_SURFACE_GROWTHS: ConfiguredFeature<*, *>
        private set

    /**
     * Configured refuse pile feature.
     */
    lateinit var REFUSE_PILE: ConfiguredFeature<*, *>
        private set

    /**
     * Configured plassein growth.
     */
    lateinit var PLASSEIN_GROWTH: ConfiguredFeature<*, *>
        private set

    /**
     * Configured crystal growth.
     */
    lateinit var CRYSTAL_GROWTH: ConfiguredFeature<*, *>
        private set

    /**
     * Creates leylines that stretch throughout a chunk, connecting to leylines in adjacent chunks.
     */
    lateinit var LEYLINES: ConfiguredFeature<DefaultFeatureConfig, *>
        private set

    /**
     * Configured server tower.
     */
    lateinit var SERVER_TOWER: ConfiguredFeature<*, *>
        private set

    /**
     * Configured transmission tower.
     */
    lateinit var TRANSMISSION_TOWER: ConfiguredFeature<*, *>
        private set

    /**
     * Configured Nectere portal feature.
     */
    lateinit var NON_NECTERE_SIDE_NECTERE_PORTAL: RegistryKey<ConfiguredFeature<*, *>>
        private set

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
     * Registers the configured features and adds nectere portals to every biome.
     */
    fun register() {
        PLASSEIN_SURFACE_GROWTH = register(
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

        FOREST_SURFACE_GROWTHS = register(
            "forest_surface_growths", PLASSEIN_SURFACE_GROWTH.decorate(
                HotMDecorators.COUNT_CHANCE_HEIGHTMAP_IN_RANGE.configure(
                    CountChanceInRangeDecoratorConfig(100, 200, 24, 0.5f)
                )
            )
        )

        REFUSE_PILE = register(
            "refuse_pile", HotMFeatures.REFUSE_PILE.configure(PileFeatureConfig(THINKING_STONE, 0))
                .decorate(FeatureAccess.getSquareHeightmap()).repeatRandomly(2)
        )

        PLASSEIN_GROWTH = register(
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

        CRYSTAL_GROWTH = register(
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
            ).uniformRange(YOffset.fixed(15), YOffset.fixed(134)).spreadHorizontally().repeat(10)
        )

        LEYLINES = register("leylines", HotMFeatures.LEYLINE.configure(FeatureConfig.DEFAULT))

        SERVER_TOWER = register(
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

        TRANSMISSION_TOWER = register(
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

        NON_NECTERE_SIDE_NECTERE_PORTAL =
            registerKey(
                "nns_nectere_portal",
                HotMFeatures.NON_NECTERE_SIDE_NECTERE_PORTAL.configure(FeatureConfig.DEFAULT)
            )

        // Add the portal feature to every biome. The biome validity is only checked once the spawner BE tries to
        // generate the actual portal.
        BiomeModifications.addFeature(
            { true },
            GenerationStep.Feature.SURFACE_STRUCTURES,
            NON_NECTERE_SIDE_NECTERE_PORTAL
        )
    }

    /**
     * Used for registering configured features.
     */
    private fun <FC : FeatureConfig> register(
        name: String,
        config: ConfiguredFeature<FC, *>
    ): ConfiguredFeature<FC, *> {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, HotMConstants.identifier(name), config)
    }

    /**
     * Used for registering configured features.
     */
    private fun registerKey(
        name: String,
        config: ConfiguredFeature<*, *>
    ): RegistryKey<ConfiguredFeature<*, *>> {
        val id = HotMConstants.identifier(name)
        val key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, id)
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, config)
        return key
    }
}