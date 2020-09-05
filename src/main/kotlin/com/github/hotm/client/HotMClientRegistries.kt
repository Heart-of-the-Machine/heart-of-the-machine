package com.github.hotm.client

import com.github.hotm.HotMConstants
import com.github.hotm.client.blockmodel.HotMBlockModel
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HotMClientRegistries {
    // identifiers

    val BLOCK_MODEL_IDENTIFIER = HotMConstants.identifier("block_model")

    // keys

    val BLOCK_MODEL_KEY = RegistryKey.ofRegistry<Codec<out HotMBlockModel>>(BLOCK_MODEL_IDENTIFIER)

    // registries

    val BLOCK_MODEL = Registry.register(
        Registry.REGISTRIES as Registry<in Registry<*>>,
        BLOCK_MODEL_IDENTIFIER,
        SimpleRegistry(BLOCK_MODEL_KEY, Lifecycle.experimental())
    )
}