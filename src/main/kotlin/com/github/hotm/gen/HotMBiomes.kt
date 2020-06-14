package com.github.hotm.gen

import com.github.hotm.HotMConstants
import com.github.hotm.gen.biome.ThinkingForestBiome
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Registers biomes for the Nectere dimension.
 */
object HotMBiomes {
    /**
     * The thinking forest biome.
     */
    val THINKING_FOREST = ThinkingForestBiome()

    /**
     * Does the registering of biomes.
     */
    fun register() {
        Registry.register(Registry.BIOME, Identifier(HotMConstants.MOD_ID, "thinking_forest"), THINKING_FOREST)
    }
}