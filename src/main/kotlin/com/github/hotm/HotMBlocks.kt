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
     * Glowy Obelisk part block.
     */
    val GLOWY_OBELISK_PART = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f))

    /**
     * Machine Casing block.
     */
    val MACHINE_CASING = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f))

    /**
     * Metal Machine Casing block.
     */
    val METAL_MACHINE_CASING = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f))

    /**
     * Obelisk part block.
     */
    val OBELISK_PART = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f))

    /**
     * Plassein Bloom block.
     */
    val PLASSEIN_BLOOM = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 10.0f).nonOpaque())

    /**
     * Plassein Machine Casing block.
     */
    val PLASSEIN_MACHINE_CASING = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 10.0f))

    /**
     * Plassein Stem Casing block.
     */
    val PLASSEIN_STEM = Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 10.0f))

    /**
     * Register all Heart of the Machine blocks...
     */
    fun register() {
        registerAll(
            THINKING_STONE to "thinking_stone" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            TEST_MACHINE_CASING to "test_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            GLOWY_OBELISK_PART to "glowy_obelisk_part" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            MACHINE_CASING to "machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            METAL_MACHINE_CASING to "metal_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            OBELISK_PART to "obelisk_part" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_BLOOM to "plassein_bloom" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_MACHINE_CASING to "plassein_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_STEM to "plassein_stem" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS)
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