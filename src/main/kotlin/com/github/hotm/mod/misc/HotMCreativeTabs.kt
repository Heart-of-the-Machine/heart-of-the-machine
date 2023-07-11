package com.github.hotm.mod.misc

import com.github.hotm.mod.Constants
import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks.GLOWY_OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_SLAB
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_STAIRS
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_LEAVES
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_STEM
import com.github.hotm.mod.block.HotMBlocks.THINKING_SAND
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICKS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_STAIRS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILES
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_STAIRS
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object HotMCreativeTabs {
    val MAIN by lazy {
        FabricItemGroup.builder().name(Constants.tt("itemGroup", "main")).entries { _, collector ->
            collector.addItems(
                THINKING_STONE,
                THINKING_SCRAP,
                RUSTED_THINKING_SCRAP,
                PLASSEIN_THINKING_SCRAP,

                SMOOTH_THINKING_STONE,
                THINKING_STONE_BRICKS,
                THINKING_STONE_TILES,
                SMOOTH_THINKING_STONE_STAIRS,
                THINKING_STONE_BRICK_STAIRS,
                THINKING_STONE_TILE_STAIRS,
                SMOOTH_THINKING_STONE_SLAB,
                THINKING_STONE_BRICK_SLAB,
                THINKING_STONE_TILE_SLAB,

                OBELISK_PART,
                GLOWY_OBELISK_PART,

                THINKING_SAND,

                SOLAR_ARRAY_STEM,
                SOLAR_ARRAY_LEAVES,

                THINKING_STONE_LEYLINE,
                THINKING_SCRAP_LEYLINE,
                RUSTED_THINKING_SCRAP_LEYLINE,
                PLASSEIN_THINKING_SCRAP_LEYLINE,
                SMOOTH_THINKING_STONE_LEYLINE
            )
        }.build()
    }

    fun init() {
        Registry.register(Registries.ITEM_GROUP, id("main"), MAIN)
    }

    private fun ItemGroup.ItemStackCollector.addItems(vararg items: ItemConvertible) {
        for (item in items) {
            addItem(item)
        }
    }
}
