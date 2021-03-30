package com.github.hotm

import com.github.hotm.util.CardinalDirection
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.gen.feature.segment.FeatureSegmentType
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HotMRegistries {
    // identifiers

    val UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("unit_feature_segment_type")
    val CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("cardinal_feature_segment_type")
    val AURA_NODE_TYPE_IDENTIFIER = HotMConstants.identifier("aura_node_type")

    // keys

    val UNIT_FEATURE_SEGMENT_TYPE_KEY =
        RegistryKey.ofRegistry<FeatureSegmentType<Unit, *>>(UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    val CARDINAL_FEATURE_SEGMENT_TYPE_KEY =
        RegistryKey.ofRegistry<FeatureSegmentType<CardinalDirection, *>>(CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    val AURA_NODE_TYPE_KEY = RegistryKey.ofRegistry<AuraNodeType<out AuraNode>>(AURA_NODE_TYPE_IDENTIFIER)

    // registries

    val UNIT_FEATURE_SEGMENT_TYPE = SimpleRegistry(UNIT_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
    val CARDINAL_FEATURE_SEGMENT_TYPE = SimpleRegistry(CARDINAL_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
    val AURA_NODE_TYPE = SimpleRegistry(AURA_NODE_TYPE_KEY, Lifecycle.experimental())

    fun register() {
        Registry.register(
            Registry.REGISTRIES as Registry<in Registry<*>>,
            UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER,
            UNIT_FEATURE_SEGMENT_TYPE
        )
        Registry.register(
            Registry.REGISTRIES as Registry<Registry<*>>,
            CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER,
            CARDINAL_FEATURE_SEGMENT_TYPE
        )
        Registry.register(
            Registry.REGISTRIES as Registry<Registry<*>>,
            AURA_NODE_TYPE_IDENTIFIER,
            AURA_NODE_TYPE
        )
    }
}