package com.github.hotm

import com.github.hotm.blocks.NecterePortalBlock
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.PillarBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Static block access and initialization.
 */
object HotMBlocks {
    /**
     * Glowy Obelisk part block.
     */
    val GLOWY_OBELISK_PART =
        PillarBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Machine Casing block.
     */
    val MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Machine Casing Bricks block.
     */
    val MACHINE_CASING_BRICKS =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Metal Machine Casing block.
     */
    val METAL_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Nectere Portal block.
     */
    val NECTERE_PORTAL = NecterePortalBlock(
        FabricBlockSettings.of(Material.PORTAL).noCollision().strength(-1.0f).sounds(BlockSoundGroup.GLASS).nonOpaque()
            .lightLevel { 3 })

    /**
     * Obelisk part block.
     */
    val OBELISK_PART =
        PillarBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Plassein Bloom block.
     */
    val PLASSEIN_BLOOM = Block(
        FabricBlockSettings.of(Material.LEAVES).requiresTool().strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOL)
            .nonOpaque()
    )

    /**
     * Plassein Machine Casing block.
     */
    val PLASSEIN_MACHINE_CASING = Block(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(31.0f, 10.0f).sounds(BlockSoundGroup.METAL)
    )

    /**
     * Plassein Stem Casing block.
     */
    val PLASSEIN_STEM =
        PillarBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOD))

    /**
     * Rusted Machine Casing block.
     */
    val RUSTED_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Smooth Thinking Stone block.
     */
    val SMOOTH_THINKING_STONE =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Surface Machine Casing block.
     */
    val SURFACE_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Temp machine casing block.
     */
    val TEST_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))

    /**
     * Thinking stone, dimension base block.
     */
    val THINKING_STONE =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Thinking Stone Bricks block.
     */
    val THINKING_STONE_BRICKS =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Thinking Stone Tiles block.
     */
    val THINKING_STONE_TILES =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))

    /**
     * Register all Heart of the Machine blocks...
     */
    fun register() {
        registerAll(
            GLOWY_OBELISK_PART to "glowy_obelisk_part" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            MACHINE_CASING to "machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            MACHINE_CASING_BRICKS to "machine_casing_bricks" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            METAL_MACHINE_CASING to "metal_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            NECTERE_PORTAL to "nectere_portal" to Item.Settings(),
            OBELISK_PART to "obelisk_part" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_BLOOM to "plassein_bloom" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_MACHINE_CASING to "plassein_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            PLASSEIN_STEM to "plassein_stem" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            RUSTED_MACHINE_CASING to "rusted_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            SMOOTH_THINKING_STONE to "smooth_thinking_stone" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            SURFACE_MACHINE_CASING to "surface_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            TEST_MACHINE_CASING to "test_machine_casing" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            THINKING_STONE to "thinking_stone" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            THINKING_STONE_BRICKS to "thinking_stone_bricks" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS),
            THINKING_STONE_TILES to "thinking_stone_tiles" to Item.Settings().group(ItemGroup.BUILDING_BLOCKS)
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