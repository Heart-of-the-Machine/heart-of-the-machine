package com.github.hotm.world.gen.feature

import com.github.hotm.HotMConstants
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
     * Creates leylines that stretch throughout a chunk, connecting to leylines in adjacent chunks.
     */
    val LEYLINE = register("leyline", LeylineFeature(DefaultFeatureConfig.CODEC))

    /**
     * Creates server tower like structures.
     */
    val SERVER_TOWER = register("server_tower", ServerTowerFeature(ServerTowerConfig.CODEC))

    /**
     * Creates metal towers with blinking lights.
     */
    val TRANSMISSION_TOWER = register("transmission_tower", TransmissionTowerFeature(TransmissionTowerConfig.CODEC))

    /**
     * Nectere portal feature. (Only intended to be used in dimensions that are not the Nectere dimension.)
     */
    val NON_NECTERE_SIDE_NECTERE_PORTAL =
        register("nns_nectere_portal", NecterePortalFeature(DefaultFeatureConfig.CODEC))

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
}