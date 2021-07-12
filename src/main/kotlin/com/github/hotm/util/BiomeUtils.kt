package com.github.hotm.util

import com.github.hotm.config.HotMBiomesConfig
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object BiomeUtils {
    fun checkNonNectereBiome(nonNectereWorld: ServerWorld, nonNecterePos: BlockPos): Boolean {
        val biomeId = nonNectereWorld.getBiomeKey(nonNecterePos).orElse(null)?.value

        return biomeId != null && !HotMBiomesConfig.CONFIG.necterePortalDenyBiomes!!.contains(
            biomeId.toString()
        )
    }
}