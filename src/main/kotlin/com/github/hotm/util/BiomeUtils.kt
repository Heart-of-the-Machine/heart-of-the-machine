package com.github.hotm.util

import com.github.hotm.config.HotMBiomesConfig
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RegistryWorldView

object BiomeUtils {
    /**
     * Checks all biomes in the non-nectere world at the x and z locations to see if any of the biomes allow portal
     * generation.
     *
     * @return true if any biome was found that allowed portal generation.
     */
    fun checkNonNectereBiomes(nonNectereWorld: RegistryWorldView, nonNecterePos: BlockPos): Boolean {
        val mutable = nonNecterePos.mutableCopy()

        // Biomes are only stored in steps of 4
        for (y in nonNectereWorld.bottomY..nonNectereWorld.topY step 4) {
            mutable.y = y
            if (checkNonNectereBiome(nonNectereWorld, mutable)) {
                return true
            }
        }

        return false
    }

    /**
     * Checks the biome in the non-nectere world at the given location to see if it allows portal generation.
     *
     * @return true if portal generation is allowed.
     */
    fun checkNonNectereBiome(nonNectereWorld: RegistryWorldView, nonNecterePos: BlockPos): Boolean {
        val biomeId = nonNectereWorld.getBiomeKey(nonNecterePos).orElse(null)?.value

        return biomeId != null && !HotMBiomesConfig.CONFIG.necterePortalDenyBiomes!!.contains(
            biomeId.toString()
        )
    }
}