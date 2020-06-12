package com.github.hotm

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Static block access and initialization.
 */
object HotMBlocks {
    /**
     * Thinking stone, dimension base block.
     */
    val THINKING_STONE = Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f))

    /**
     * Temp machine casing block.
     */
    val TEST_MACHINE_CASING = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f))

    /**
     * Register all Heart of the Machine blocks...
     */
    fun register() {
        registerAll(
            THINKING_STONE to "thinking_stone" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            TEST_MACHINE_CASING to "test_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS)
        )
    }

    private fun registerAll(vararg blocks: Pair<Pair<Block, String>, Item.Settings>) {
        for (block in blocks) {
            Registry.register(Registry.BLOCK, Identifier(HotMConstants.MOD_ID, block.first.second), block.first.first)
            Registry.register(
                Registry.ITEM,
                Identifier(HotMConstants.MOD_ID, block.first.second),
                BlockItem(block.first.first, block.second)
            )
        }
    }
}