package com.github.hotm.mod.misc

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup

object HotMCreativeTabs {
    fun init() {
        FabricItemGroup.builder(id("main")).entries { _, collector ->
            collector.addItems(THINKING_STONE, THINKING_SCRAP, RUSTED_THINKING_SCRAP, PLASSEIN_THINKING_SCRAP)
        }.build()
    }

    private fun ItemGroup.ItemStackCollector.addItems(vararg items: ItemConvertible) {
        for (item in items) {
            addItem(item)
        }
    }
}
