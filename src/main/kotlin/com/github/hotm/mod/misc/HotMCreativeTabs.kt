package com.github.hotm.mod.misc

import com.github.hotm.mod.Constants
import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup

object HotMCreativeTabs {
    fun init() {
        FabricItemGroup.builder().name(Constants.tt("itemGroup", "main")).entries { _, collector ->
            collector.addItems(
                THINKING_STONE,
                THINKING_STONE_LEYLINE,
                THINKING_SCRAP,
                THINKING_SCRAP_LEYLINE,
                RUSTED_THINKING_SCRAP,
                RUSTED_THINKING_SCRAP_LEYLINE,
                PLASSEIN_THINKING_SCRAP,
                PLASSEIN_THINKING_SCRAP_LEYLINE
            )
        }.build()
    }

    private fun ItemGroup.ItemStackCollector.addItems(vararg items: ItemConvertible) {
        for (item in items) {
            addItem(item)
        }
    }
}
