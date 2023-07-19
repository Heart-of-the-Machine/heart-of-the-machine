package com.github.hotm.mod.item

import com.github.hotm.mod.Constants
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object HotMItems {
    private val RESOURCE_ITEM_SETTINGS = QuiltItemSettings()

    val AURA_CRYSTAL_SHARD by lazy { Item(RESOURCE_ITEM_SETTINGS) }
    val HOLO_CRYSTAL_SHARD by lazy { Item(RESOURCE_ITEM_SETTINGS) }

    fun init() {
        register(AURA_CRYSTAL_SHARD, "aura_crystal_shard")
        register(HOLO_CRYSTAL_SHARD, "holo_crystal_shard")
    }

    private fun register(item: Item, path: String) {
        Registry.register(Registries.ITEM, Constants.id(path), item)
    }
}
