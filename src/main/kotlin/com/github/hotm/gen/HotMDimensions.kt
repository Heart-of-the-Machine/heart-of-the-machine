package com.github.hotm.gen

import com.github.hotm.HotMConstants
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

/**
 * Initializes and registers dimension functionality.
 */
object HotMDimensions {
    /**
     * Key used to reference the Nectere dimension.
     */
    val NECTERE_KEY: RegistryKey<World> =
        RegistryKey.of(Registry.DIMENSION, Identifier(HotMConstants.MOD_ID, "nectere"))

    /**
     * Registers the world generator for the Nectere dimension.
     */
    fun register() {
        // Unused chunk generator
        Registry.register(
            Registry.CHUNK_GENERATOR,
            Identifier(HotMConstants.MOD_ID, "nectere"),
            NectereChunkGenerator.CODEC
        )
    }
}