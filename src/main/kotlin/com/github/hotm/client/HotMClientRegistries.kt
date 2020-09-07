package com.github.hotm.client

import com.github.hotm.HotMConstants
import com.github.hotm.client.blockmodel.UnbakedModel
import com.github.hotm.client.blockmodel.UnbakedModelLayer
import com.github.hotm.client.blockmodel.connector.ModelConnector
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HotMClientRegistries {
    // identifiers

    val BLOCK_MODEL_IDENTIFIER = HotMConstants.identifier("block_model")
    val BLOCK_MODEL_LAYER_IDENTIFIER = HotMConstants.identifier("block_model_layer")
    val BLOCK_MODEL_CONNECTOR_IDENTIFIER = HotMConstants.identifier("block_model_connector")

    // keys

    val BLOCK_MODEL_KEY = RegistryKey.ofRegistry<Codec<out UnbakedModel>>(BLOCK_MODEL_IDENTIFIER)
    val BLOCK_MODEL_LAYER_KEY = RegistryKey.ofRegistry<Codec<out UnbakedModelLayer>>(BLOCK_MODEL_LAYER_IDENTIFIER)
    val BLOCK_MODEL_CONNECTOR_KEY = RegistryKey.ofRegistry<ModelConnector>(BLOCK_MODEL_CONNECTOR_IDENTIFIER)

    // registries

    val BLOCK_MODEL: Registry<Codec<out UnbakedModel>> = Registry.register(
        Registry.REGISTRIES as Registry<in Registry<*>>,
        BLOCK_MODEL_IDENTIFIER,
        SimpleRegistry(BLOCK_MODEL_KEY, Lifecycle.experimental())
    )
    val BLOCK_MODEL_LAYER: Registry<Codec<out UnbakedModelLayer>> = Registry.register(
        Registry.REGISTRIES as Registry<in Registry<*>>,
        BLOCK_MODEL_LAYER_IDENTIFIER,
        SimpleRegistry(BLOCK_MODEL_LAYER_KEY, Lifecycle.experimental())
    )
    val BLOCK_MODEL_CONNECTOR: Registry<ModelConnector> = Registry.register(
        Registry.REGISTRIES as Registry<in Registry<*>>,
        BLOCK_MODEL_CONNECTOR_IDENTIFIER,
        SimpleRegistry(BLOCK_MODEL_CONNECTOR_KEY, Lifecycle.experimental())
    )
}