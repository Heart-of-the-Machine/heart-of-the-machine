package com.github.hotm

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

object HotMItems {
    val HOTM_BUILDING_ITEM_GROUP =
        FabricItemGroupBuilder.build(HotMConstants.identifier("building"), HotMItems::buildingGroupItem)
    val HOTM_MATERIAL_ITEM_GROUP =
        FabricItemGroupBuilder.build(HotMConstants.identifier("materials"), HotMItems::materialGroupItem)

    val HOTM_BUILDING_ITEM_SETTINGS: Item.Settings = Item.Settings().group(HOTM_BUILDING_ITEM_GROUP)
    val HOTM_MATERIAL_ITEM_SETTINGS: Item.Settings = Item.Settings().group(HOTM_MATERIAL_ITEM_GROUP)

    val CYAN_CRYSTAL_SHARD = Item(HOTM_MATERIAL_ITEM_SETTINGS)
    val MAGENTA_CRYSTAL_SHARD = Item(HOTM_MATERIAL_ITEM_SETTINGS)
    val PLASSEIN_FUEL_CHUNK = Item(HOTM_MATERIAL_ITEM_SETTINGS)

    fun register() {
        register(CYAN_CRYSTAL_SHARD, "cyan_crystal_shard")
        register(MAGENTA_CRYSTAL_SHARD, "magenta_crystal_shard")
        register(PLASSEIN_FUEL_CHUNK, "plassein_fuel_chunk")
    }

    private fun register(item: Item, name: String) {
        Registry.register(Registry.ITEM, HotMConstants.identifier(name), item)
    }

    private fun buildingGroupItem(): ItemStack {
        return ItemStack(HotMBlocks.CYAN_THINKING_STONE_LAMP)
    }

    private fun materialGroupItem(): ItemStack {
        return ItemStack(CYAN_CRYSTAL_SHARD)
    }
}