package com.github.hotm.world

import com.github.hotm.world.gen.HotMBiomes
import com.github.hotm.world.gen.biome.NectereBiomeData
import com.google.common.collect.HashMultimap
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import java.util.stream.Stream

object HotMPortalableBiomes {
    private val NECTERE_PORTAL_BIOMES = HashMultimap.create<RegistryKey<World>, NectereBiomeData>()

    /**
     * Collects all nectere biome data from HotMBiomes and performs mod integration.
     */
    fun findBiomes() {
        for (biomeData in HotMBiomes.biomeData().values) {
            if (biomeData.isPortalable) {
                NECTERE_PORTAL_BIOMES.put(biomeData.targetWorld, biomeData)
            }
        }

        // TODO NectereBiomeData mod-compat/API
    }

    fun stream(targetWorld: RegistryKey<World>): Stream<NectereBiomeData> {
        return NECTERE_PORTAL_BIOMES.get(targetWorld).stream()
    }
}