package com.github.hotm.gen

import com.github.hotm.HotMConstants
import com.github.hotm.gen.biome.ThinkingForestBiome
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome

/**
 * Registers biomes for the Nectere dimension.
 */
object HotMBiomes {
    private val NECTERE_PORTAL_BIOMES = mutableListOf<Identifier>()
    private val BIOMES = mutableMapOf<Identifier, Biome>()

    /**
     * The thinking forest biome.
     */
    val THINKING_FOREST = setup(ThinkingForestBiome(), HotMConstants.identifier("thinking_forest"), true)

    /**
     * Does the registering of biomes.
     */
    fun register() {
        for (biome in BIOMES) {
            Registry.register(Registry.BIOME, biome.key, biome.value)
        }
    }

    /**
     * Gets the list of biomes from HotM that should contain a Nectere portal.
     */
    fun necterePortalBiomes(): List<Identifier> {
        return NECTERE_PORTAL_BIOMES
    }

    private fun <B : Biome> setup(biome: B, name: Identifier, portalable: Boolean): B {
        BIOMES[name] = biome

        if (portalable) {
            NECTERE_PORTAL_BIOMES += name
        }

        return biome
    }
}