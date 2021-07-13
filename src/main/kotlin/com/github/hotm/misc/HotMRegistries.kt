package com.github.hotm.misc

import com.github.hotm.HotMConstants
import com.github.hotm.util.CardinalDirection
import com.github.hotm.world.gen.feature.segment.FeatureSegmentType
import com.mojang.serialization.Lifecycle
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HotMRegistries {
    // identifiers

    val UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("unit_feature_segment_type")
    val CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("cardinal_feature_segment_type")

    // keys

    val UNIT_FEATURE_SEGMENT_TYPE_KEY by lazy {
        RegistryKey.ofRegistry<FeatureSegmentType<Unit, *>>(UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    }
    val CARDINAL_FEATURE_SEGMENT_TYPE_KEY by lazy {
        RegistryKey.ofRegistry<FeatureSegmentType<CardinalDirection, *>>(CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    }

    // registries

    lateinit var UNIT_FEATURE_SEGMENT_TYPE: Registry<FeatureSegmentType<Unit, *>>
        private set
    lateinit var CARDINAL_FEATURE_SEGMENT_TYPE: Registry<FeatureSegmentType<CardinalDirection, *>>
        private set

    fun register() {
        UNIT_FEATURE_SEGMENT_TYPE = Registry.register(
            Registry.REGISTRIES as Registry<in Registry<*>>,
            UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER,
            SimpleRegistry(UNIT_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
        )
        CARDINAL_FEATURE_SEGMENT_TYPE = Registry.register(
            Registry.REGISTRIES as Registry<Registry<*>>,
            CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER,
            SimpleRegistry(CARDINAL_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
        )
    }
}