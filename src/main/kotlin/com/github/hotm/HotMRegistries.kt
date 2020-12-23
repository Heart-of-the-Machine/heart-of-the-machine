package com.github.hotm

import com.github.hotm.world.gen.feature.segment.FeatureSegmentType
import com.github.hotm.util.CardinalDirection
import com.github.hotm.world.auranet.AuraNetNode
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HotMRegistries {
    // identifiers

    val UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("unit_feature_segment_type")
    val CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER = HotMConstants.identifier("cardinal_feature_segment_type")
    val AURA_NET_NODE_IDENTIFIER = HotMConstants.identifier("aura_net_node")

    // keys

    val UNIT_FEATURE_SEGMENT_TYPE_KEY =
        RegistryKey.ofRegistry<FeatureSegmentType<Unit, *>>(UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    val CARDINAL_FEATURE_SEGMENT_TYPE_KEY =
        RegistryKey.ofRegistry<FeatureSegmentType<CardinalDirection, *>>(CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER)
    val AURA_NET_NODE_KEY = RegistryKey.ofRegistry<(Runnable) -> Codec<out AuraNetNode>>(AURA_NET_NODE_IDENTIFIER)

    // registries

    val UNIT_FEATURE_SEGMENT_TYPE = Registry.register(
        Registry.REGISTRIES as Registry<in Registry<*>>,
        UNIT_FEATURE_SEGMENT_TYPE_IDENTIFIER,
        SimpleRegistry(UNIT_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
    )
    val CARDINAL_FEATURE_SEGMENT_TYPE = Registry.register(
        Registry.REGISTRIES as Registry<Registry<*>>,
        CARDINAL_FEATURE_SEGMENT_TYPE_IDENTIFIER,
        SimpleRegistry(CARDINAL_FEATURE_SEGMENT_TYPE_KEY, Lifecycle.experimental())
    )
    val AURA_NET_NODE = Registry.register(
        Registry.REGISTRIES as Registry<Registry<*>>,
        AURA_NET_NODE_IDENTIFIER,
        SimpleRegistry(AURA_NET_NODE_KEY, Lifecycle.experimental())
    )
}