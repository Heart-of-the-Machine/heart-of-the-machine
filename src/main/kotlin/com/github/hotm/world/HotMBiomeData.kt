package com.github.hotm.world

import com.github.hotm.world.gen.biome.NectereBiomeData
import com.google.common.collect.HashMultimap
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import java.util.*
import java.util.stream.Stream

object HotMBiomeData {
    private val PORTAL_BIOMES_BY_WORLD = HashMultimap.create<RegistryKey<World>, NectereBiomeData>()
    private val BIOMES_BY_ID = hashMapOf<RegistryKey<Biome>, NectereBiomeData>()

    /**
     * Adds Nectere biome data to be associated with the data's biome key.
     */
    fun addBiomeData(biomeData: NectereBiomeData) {
        if (biomeData.isPortalable) {
            PORTAL_BIOMES_BY_WORLD.put(biomeData.targetWorld, biomeData)
        }
        BIOMES_BY_ID[biomeData.biome] = biomeData
    }

    /**
     * Gets the map of
     */
    fun getDataById(): Map<RegistryKey<Biome>, NectereBiomeData> = BIOMES_BY_ID

    fun <T> ifData(optionalBiome: Optional<RegistryKey<Biome>>, then: (NectereBiomeData) -> T): T? {
        if (optionalBiome.isEmpty) {
            return null
        }

        val biome = optionalBiome.get()

        return if (BIOMES_BY_ID.containsKey(biome)) {
            then(BIOMES_BY_ID[biome]!!)
        } else {
            null
        }
    }

    fun <T> ifPortalable(biome: Optional<RegistryKey<Biome>>, then: (NectereBiomeData) -> T): T? {
        return ifData(biome) {
            if (it.isPortalable) then(it) else null
        }
    }

    fun streamPortalables(targetWorld: RegistryKey<World>): Stream<NectereBiomeData> {
        return PORTAL_BIOMES_BY_WORLD.get(targetWorld).stream()
    }
}