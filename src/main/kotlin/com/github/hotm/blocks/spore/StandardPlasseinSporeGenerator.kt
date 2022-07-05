package com.github.hotm.blocks.spore

import com.github.hotm.world.gen.feature.HotMConfiguredFeatures
import net.minecraft.util.math.random.Random
import net.minecraft.world.gen.feature.ConfiguredFeature

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
