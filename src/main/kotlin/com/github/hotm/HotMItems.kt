package com.github.hotm

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object HotMItems {
    val HOTM_BUILDING_ITEM_GROUP =
        FabricItemGroupBuilder.build(HotMConstants.identifier("building"), HotMItems::buildingGroupItem)

    val HOTM_BUILDING_ITEM_SETTINGS = Item.Settings().group(HOTM_BUILDING_ITEM_GROUP)

    fun register() {}

    private fun buildingGroupItem(): ItemStack {
        return ItemStack(HotMBlocks.CYAN_THINKING_STONE_LAMP)
    }
}