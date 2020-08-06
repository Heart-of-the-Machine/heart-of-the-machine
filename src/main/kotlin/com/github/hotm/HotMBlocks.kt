package com.github.hotm

import com.github.hotm.blocks.NecterePortalBlock
import com.github.hotm.mixinapi.BlockCreators
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.PillarBlock
import net.minecraft.block.SlabBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry

/**
 * Static block access and initialization.
 */
object HotMBlocks {
    private val BUILDING_BLOCKS = Item.Settings().group(ItemGroup.BUILDING_BLOCKS)

    /*
     * Crystals & Lamps.
     */
    val CYAN_CRYSTAL = Block(
        FabricBlockSettings.of(Material.GLASS).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.GLASS)
            .lightLevel(15)
    )
    val CYAN_CRYSTAL_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )
    val CYAN_MACHINE_CASING_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )
    val CYAN_THINKING_STONE_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )
    val MAGENTA_CRYSTAL = Block(
        FabricBlockSettings.of(Material.GLASS).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.GLASS)
            .lightLevel(15)
    )
    val MAGENTA_CRYSTAL_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )
    val MAGENTA_MACHINE_CASING_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )
    val MAGENTA_THINKING_STONE_LAMP = Block(
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    )

    /*
     * Obelisk parts.
     */
    val GLOWY_OBELISK_PART =
        PillarBlock(
            FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE)
        )
    val OBELISK_PART =
        PillarBlock(
            FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE)
        )
    val NECTERE_PORTAL = NecterePortalBlock(
        FabricBlockSettings.of(Material.PORTAL).noCollision().strength(-1.0f).sounds(BlockSoundGroup.GLASS).nonOpaque()
            .lightLevel { 12 })

    /*
     * Machine Casing Blocks.
     */
    val MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val MACHINE_CASING_BRICKS =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val METAL_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val PLASSEIN_MACHINE_CASING = Block(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(31.0f, 10.0f).sounds(BlockSoundGroup.METAL)
    )
    val RUSTED_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val SURFACE_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val TEST_MACHINE_CASING =
        Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL))
    val MACHINE_CASING_STAIRS =
        BlockCreators.createStairsBlock(MACHINE_CASING.defaultState, FabricBlockSettings.copyOf(MACHINE_CASING))
    val MACHINE_CASING_BRICK_STAIRS = BlockCreators.createStairsBlock(
        MACHINE_CASING_BRICKS.defaultState,
        FabricBlockSettings.copyOf(MACHINE_CASING_BRICKS)
    )
    val MACHINE_CASING_SLAB = SlabBlock(FabricBlockSettings.copyOf(MACHINE_CASING))
    val MACHINE_CASING_BRICK_SLAB = SlabBlock(FabricBlockSettings.copyOf(MACHINE_CASING_BRICKS))

    /*
     * Plassein Growth Blocks.
     */
    val PLASSEIN_BLOOM = Block(
        FabricBlockSettings.of(Material.LEAVES).requiresTool().strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOL)
            .nonOpaque()
    )
    val PLASSEIN_STEM = PillarBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOD)
    )

    /*
     * Thinking Stone Blocks.
     */
    val SMOOTH_THINKING_STONE =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))
    val THINKING_STONE =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))
    val THINKING_STONE_BRICKS =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))
    val THINKING_STONE_TILES =
        Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE))
    val SMOOTH_THINKING_STONE_STAIRS = BlockCreators.createStairsBlock(
        SMOOTH_THINKING_STONE.defaultState,
        FabricBlockSettings.copyOf(SMOOTH_THINKING_STONE)
    )
    val THINKING_STONE_STAIRS =
        BlockCreators.createStairsBlock(THINKING_STONE.defaultState, FabricBlockSettings.copyOf(THINKING_STONE))
    val THINKING_STONE_BRICK_STAIRS = BlockCreators.createStairsBlock(
        THINKING_STONE_BRICKS.defaultState,
        FabricBlockSettings.copyOf(THINKING_STONE_BRICKS)
    )
    val THINKING_STONE_TILE_STAIRS = BlockCreators.createStairsBlock(
        THINKING_STONE_TILES.defaultState,
        FabricBlockSettings.copyOf(THINKING_STONE_TILES)
    )
    val SMOOTH_THINKING_STONE_SLAB = SlabBlock(FabricBlockSettings.copyOf(SMOOTH_THINKING_STONE))
    val THINKING_STONE_SLAB = SlabBlock(FabricBlockSettings.copyOf(THINKING_STONE))
    val THINKING_STONE_BRICK_SLAB = SlabBlock(FabricBlockSettings.copyOf(THINKING_STONE_BRICKS))
    val THINKING_STONE_TILE_SLAB = SlabBlock(FabricBlockSettings.copyOf(THINKING_STONE_TILES))

    /**
     * Register all Heart of the Machine blocks...
     */
    fun register() {
        registerAll(
            CYAN_CRYSTAL to "cyan_crystal" to BUILDING_BLOCKS,
            CYAN_CRYSTAL_LAMP to "cyan_crystal_lamp" to BUILDING_BLOCKS,
            CYAN_MACHINE_CASING_LAMP to "cyan_machine_casing_lamp" to BUILDING_BLOCKS,
            CYAN_THINKING_STONE_LAMP to "cyan_thinking_stone_lamp" to BUILDING_BLOCKS,
            GLOWY_OBELISK_PART to "glowy_obelisk_part" to BUILDING_BLOCKS,
            MACHINE_CASING to "machine_casing" to BUILDING_BLOCKS,
            MACHINE_CASING_SLAB to "machine_casing_slab" to BUILDING_BLOCKS,
            MACHINE_CASING_STAIRS to "machine_casing_stairs" to BUILDING_BLOCKS,
            MACHINE_CASING_BRICKS to "machine_casing_bricks" to BUILDING_BLOCKS,
            MACHINE_CASING_BRICK_SLAB to "machine_casing_brick_slab" to BUILDING_BLOCKS,
            MACHINE_CASING_BRICK_STAIRS to "machine_casing_brick_stairs" to BUILDING_BLOCKS,
            MAGENTA_CRYSTAL to "magenta_crystal" to BUILDING_BLOCKS,
            MAGENTA_CRYSTAL_LAMP to "magenta_crystal_lamp" to BUILDING_BLOCKS,
            MAGENTA_MACHINE_CASING_LAMP to "magenta_machine_casing_lamp" to BUILDING_BLOCKS,
            MAGENTA_THINKING_STONE_LAMP to "magenta_thinking_stone_lamp" to BUILDING_BLOCKS,
            METAL_MACHINE_CASING to "metal_machine_casing" to BUILDING_BLOCKS,
            NECTERE_PORTAL to "nectere_portal" to Item.Settings(),
            OBELISK_PART to "obelisk_part" to BUILDING_BLOCKS,
            PLASSEIN_BLOOM to "plassein_bloom" to BUILDING_BLOCKS,
            PLASSEIN_MACHINE_CASING to "plassein_machine_casing" to BUILDING_BLOCKS,
            PLASSEIN_STEM to "plassein_stem" to BUILDING_BLOCKS,
            RUSTED_MACHINE_CASING to "rusted_machine_casing" to BUILDING_BLOCKS,
            SMOOTH_THINKING_STONE to "smooth_thinking_stone" to BUILDING_BLOCKS,
            SMOOTH_THINKING_STONE_SLAB to "smooth_thinking_stone_slab" to BUILDING_BLOCKS,
            SMOOTH_THINKING_STONE_STAIRS to "smooth_thinking_stone_stairs" to BUILDING_BLOCKS,
            SURFACE_MACHINE_CASING to "surface_machine_casing" to BUILDING_BLOCKS,
            TEST_MACHINE_CASING to "test_machine_casing" to BUILDING_BLOCKS,
            THINKING_STONE to "thinking_stone" to BUILDING_BLOCKS,
            THINKING_STONE_SLAB to "thinking_stone_slab" to BUILDING_BLOCKS,
            THINKING_STONE_STAIRS to "thinking_stone_stairs" to BUILDING_BLOCKS,
            THINKING_STONE_BRICKS to "thinking_stone_bricks" to BUILDING_BLOCKS,
            THINKING_STONE_BRICK_SLAB to "thinking_stone_brick_slab" to BUILDING_BLOCKS,
            THINKING_STONE_BRICK_STAIRS to "thinking_stone_brick_stairs" to BUILDING_BLOCKS,
            THINKING_STONE_TILES to "thinking_stone_tiles" to BUILDING_BLOCKS,
            THINKING_STONE_TILE_SLAB to "thinking_stone_tile_slab" to BUILDING_BLOCKS,
            THINKING_STONE_TILE_STAIRS to "thinking_stone_tile_stairs" to BUILDING_BLOCKS
        )
    }

    private fun registerAll(vararg blocks: Pair<Pair<Block, String>, Item.Settings>) {
        for (block in blocks) {
            val identifier = HotMConstants.identifier(block.first.second)
            Registry.register(Registry.BLOCK, identifier, block.first.first)
            Registry.register(Registry.ITEM, identifier, BlockItem(block.first.first, block.second))
        }
    }
}