package com.github.hotm.blocks.spore

import com.github.hotm.world.gen.feature.HotMConfiguredFeatures
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.gen.feature.ConfiguredFeature

object StandardPlasseinSporeGenerator : PlasseinSporeGenerator() {
    override fun createGrowthFeature(random: RandomGenerator, leyline: Boolean): ConfiguredFeature<*, *>? {
        return null
    }

    override fun createLargeGrowthFeature(random: RandomGenerator): ConfiguredFeature<*, *>? {
        return null
    }

    override fun createCrossGrowthFeature(random: RandomGenerator, leyline: Boolean): ConfiguredFeature<*, *>? {
        return HotMConfiguredFeatures.PLASSEIN_SURFACE_GROWTH
    }
}
