package com.github.hotm.misc

import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.items.HotMItems
import net.fabricmc.fabric.api.registry.FuelRegistry

object HotMFuels {
    fun register() {
        FuelRegistry.INSTANCE.add(HotMBlocks.PLASSEIN_LOG, 300)
        FuelRegistry.INSTANCE.add(HotMBlocks.PLASSEIN_PLANKS, 300)
        FuelRegistry.INSTANCE.add(HotMItems.PLASSEIN_FUEL_CHUNK, 2000)
        FuelRegistry.INSTANCE.add(HotMBlocks.PLASSEIN_FUEL_BLOCK, 20000)
    }
}