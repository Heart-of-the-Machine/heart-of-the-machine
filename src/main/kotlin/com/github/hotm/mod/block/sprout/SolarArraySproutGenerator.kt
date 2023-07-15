package com.github.hotm.mod.block.sprout

import com.github.hotm.mod.world.gen.feature.HotMFeatures
import net.minecraft.registry.RegistryKey
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.gen.feature.ConfiguredFeature

object SolarArraySproutGenerator : PlasseinSproutGenerator() {
    override fun createGrowthFeature(random: RandomGenerator, leyline: Boolean): RegistryKey<ConfiguredFeature<*, *>>? =
        null

    override fun createLargeGrowthFeature(random: RandomGenerator): RegistryKey<ConfiguredFeature<*, *>>? = null

    override fun createCrossGrowthFeature(
        random: RandomGenerator, leyline: Boolean
    ): RegistryKey<ConfiguredFeature<*, *>>? = HotMFeatures.SOLAR_ARRAY_CONFIGURED_FEATURE
}
