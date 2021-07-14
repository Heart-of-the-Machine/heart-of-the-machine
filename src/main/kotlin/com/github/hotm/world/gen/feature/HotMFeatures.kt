package com.github.hotm.world.gen.feature

import com.github.hotm.HotMConstants
import com.github.hotm.world.gen.feature.decorator.HotMDecorators
import com.github.hotm.world.gen.feature.segment.SegmentedFeature
import com.github.hotm.world.gen.feature.segment.SegmentedFeatureConfig
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig

/**
 * Features for Heart of the Machine biomes.
 */
object HotMFeatures {

    /**
     * Creates a segmented feature such as a plassein tree or other plant.
     */
    lateinit var SEGMENTED_FEATURE: SegmentedFeature
        private set

    /**
     * Refuse pile feature. This is similar to the vanilla FOREST_ROCK feature but generates in more places.
     */
    lateinit var REFUSE_PILE: RefusePileFeature
        private set

    /**
     * Creates a plassein "tree" growth.
     */
    lateinit var PLASSEIN_GROWTH: PlasseinGrowthFeature
        private set

    /**
     * Creates crystal structures originating from walls and ceilings.
     */
    lateinit var CRYSTAL_GROWTH: CrystalGrowthFeature
        private set

    /**
     * Creates leylines that stretch throughout a chunk, connecting to leylines in adjacent chunks.
     */
    lateinit var LEYLINE: LeylineFeature
        private set

    /**
     * Creates server tower like structures.
     */
    lateinit var SERVER_TOWER: ServerTowerFeature
        private set

    /**
     * Creates metal towers with blinking lights.
     */
    lateinit var TRANSMISSION_TOWER: TransmissionTowerFeature
        private set

    /**
     * Nectere portal feature. (Only intended to be used in dimensions that are not the Nectere dimension.)
     */
    lateinit var NON_NECTERE_SIDE_NECTERE_PORTAL: NecterePortalFeature
        private set

    /**
     * Register our structures with the existing biomes.
     */
    fun register() {
        SEGMENTED_FEATURE = register("segmented_feature", SegmentedFeature(SegmentedFeatureConfig.CODEC))
        REFUSE_PILE = register("refuse_pile", RefusePileFeature(PileFeatureConfig.CODEC))
        PLASSEIN_GROWTH = register("plassein_growth", PlasseinGrowthFeature(PlasseinGrowthConfig.CODEC))
        CRYSTAL_GROWTH = register("crystal_growth", CrystalGrowthFeature(CrystalGrowthConfig.CODEC))
        LEYLINE = register("leyline", LeylineFeature(DefaultFeatureConfig.CODEC))
        SERVER_TOWER = register("server_tower", ServerTowerFeature(ServerTowerConfig.CODEC))
        TRANSMISSION_TOWER = register("transmission_tower", TransmissionTowerFeature(TransmissionTowerConfig.CODEC))
        NON_NECTERE_SIDE_NECTERE_PORTAL =
            register("nns_nectere_portal", NecterePortalFeature(DefaultFeatureConfig.CODEC))

        HotMDecorators.register()

        HotMStructureFeatures.register()

        HotMConfiguredFeatures.register()
    }

    /**
     * Used for statically registering features.
     */
    private fun <FC : FeatureConfig, F : Feature<FC>> register(name: String, feature: F): F {
        return Registry.register(Registry.FEATURE, HotMConstants.identifier(name), feature)
    }
}