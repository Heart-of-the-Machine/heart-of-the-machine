package com.github.hotm.blocks.spore

import com.github.hotm.gen.feature.HotMConfiguredFeatures
import net.minecraft.world.gen.feature.ConfiguredFeature
import java.util.*

object StandardPlasseinSporeGenerator : PlasseinSporeGenerator() {
    override fun createGrowthFeature(random: Random, leyline: Boolean): ConfiguredFeature<*, *>? {
        return null
    }

    override fun createLargeGrowthFeature(random: Random): ConfiguredFeature<*, *>? {
        return null
    }

    override fun createCrossGrowthFeature(random: Random, leyline: Boolean): ConfiguredFeature<*, *>? {
        return HotMConfiguredFeatures.PLASSEIN_SURFACE_GROWTH
    }
}