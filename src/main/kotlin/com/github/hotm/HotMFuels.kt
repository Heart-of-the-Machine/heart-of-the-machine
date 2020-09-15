package com.github.hotm

import net.fabricmc.fabric.api.registry.FuelRegistry

object HotMFuels {
    fun register() {
        FuelRegistry.INSTANCE.add(HotMBlocks.PLASSEIN_LOG, 300)
        FuelRegistry.INSTANCE.add(HotMBlocks.PLASSEIN_PLANKS, 300)
    }
}