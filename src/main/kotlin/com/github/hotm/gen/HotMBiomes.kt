package com.github.hotm.gen

import com.github.hotm.HotMConstants
import com.github.hotm.gen.biome.ThinkingForestBiome
import com.github.hotm.gen.biome.WastelandBiome
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome

/**
 * Registers biomes for the Nectere dimension.
 */
object HotMBiomes {
    private val BIOMES = mutableMapOf<Identifier, Biome>()

    /**
     * The thinking forest biome.
     */
    val THINKING_FOREST = setup(ThinkingForestBiome(), HotMConstants.identifier("thinking_forest"))

    /**
     * The wasteland biome.
     */
    val WASTELAND = setup(WastelandBiome(), HotMConstants.identifier("wasteland"))

    /**
     * Does the registering of biomes.
     */
    fun register() {
        for (biome in BIOMES) {
            Registry.register(Registry.BIOME, biome.key, biome.value)
        }
    }

    /**
     * Gets all the Nectere biomes.
     */
    fun biomes(): Map<Identifier, Biome> {
        return BIOMES
    }

    private fun <B : Biome> setup(biome: B, name: Identifier): B {
        BIOMES[name] = biome

        return biome
    }
}