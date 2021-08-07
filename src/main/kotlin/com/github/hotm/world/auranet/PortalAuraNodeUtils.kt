package com.github.hotm.world.auranet

import com.github.hotm.poi.HotMPointsOfInterest
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object PortalAuraNodeUtils {
    fun isStructureValid(pos: BlockPos, world: ServerWorld): Boolean {
        val storage = world.pointOfInterestStorage
        val portal = storage.getType(pos.down(2)).orElse(null)
        return portal == HotMPointsOfInterest.NECTERE_PORTAL
    }
}