package com.github.hotm.poi

import com.github.hotm.HotMConstants.identifier
import com.github.hotm.blocks.HotMBlocks
import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper
import net.minecraft.world.poi.PointOfInterestType

object HotMPointsOfInterest {
    lateinit var NECTERE_PORTAL: PointOfInterestType
        private set

    fun register() {
        NECTERE_PORTAL = PointOfInterestHelper.register(
            identifier("nectere_portal"),
            0,
            1,
            HotMBlocks.NECTERE_PORTAL.stateManager.states
        )
    }
}