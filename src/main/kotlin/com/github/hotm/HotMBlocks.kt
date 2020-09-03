package com.github.hotm

import com.github.hotm.blocks.NecterePortalBlock
import com.github.hotm.blocks.NecterePortalSpawnerBlock
import com.github.hotm.mixinapi.BlockCreators
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.PillarBlock
import net.minecraft.block.SlabBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry

/**
 * Static block access and initialization.
 */
object HotMBlocks {
    val HOTM_BUILDING_ITEM_GROUP =
        FabricItemGroupBuilder.build(HotMConstants.identifier("building"), HotMBlocks::mainGroupItem)

    private val HOTM_BUILDING_ITEM_SETTINGS = Item.Settings().group(HOTM_BUILDING_ITEM_GROUP)

    /*
     * Crystals & Lamps.
     */
    val CYAN_CRYSTAL = Block(
        FabricBlockSettings.of(Material.GLASS).requiresTool().breakByTool(FabricToolTags.PICKAXES).strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.GLASS).lightLevel(15)
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
        FabricBlockSettings.of(Material.GLASS).requiresTool().breakByTool(FabricToolTags.PICKAXES).strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.GLASS).lightLevel(15)
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
    val NECTERE_PORTAL_SPAWNER = NecterePortalSpawnerBlock(FabricBlockSettings.of(Material.STONE).strength(-1.0f))

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
        FabricBlockSettings.of(Material.LEAVES).strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOL)
            .nonOpaque()
    )
    val PLASSEIN_STEM = PillarBlock(
        FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.AXES).strength(1.0f, 10.0f)
            .sounds(BlockSoundGroup.WOOD)
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
        register(CYAN_CRYSTAL, "cyan_crystal", HOTM_BUILDING_ITEM_SETTINGS)
        register(CYAN_CRYSTAL_LAMP, "cyan_crystal_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(CYAN_MACHINE_CASING_LAMP, "cyan_machine_casing_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(CYAN_THINKING_STONE_LAMP, "cyan_thinking_stone_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(GLOWY_OBELISK_PART, "glowy_obelisk_part", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING, "machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING_SLAB, "machine_casing_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING_STAIRS, "machine_casing_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING_BRICKS, "machine_casing_bricks", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING_BRICK_SLAB, "machine_casing_brick_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(MACHINE_CASING_BRICK_STAIRS, "machine_casing_brick_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_CRYSTAL, "magenta_crystal", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_CRYSTAL_LAMP, "magenta_crystal_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_MACHINE_CASING_LAMP, "magenta_machine_casing_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_THINKING_STONE_LAMP, "magenta_thinking_stone_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(METAL_MACHINE_CASING, "metal_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(NECTERE_PORTAL, "nectere_portal", Item.Settings())
        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner", Item.Settings())
        register(OBELISK_PART, "obelisk_part", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_BLOOM, "plassein_bloom", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_MACHINE_CASING, "plassein_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_STEM, "plassein_stem", HOTM_BUILDING_ITEM_SETTINGS)
        register(RUSTED_MACHINE_CASING, "rusted_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE, "smooth_thinking_stone", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE_SLAB, "smooth_thinking_stone_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE_STAIRS, "smooth_thinking_stone_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(SURFACE_MACHINE_CASING, "surface_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(TEST_MACHINE_CASING, "test_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE, "thinking_stone", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_SLAB, "thinking_stone_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_STAIRS, "thinking_stone_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICKS, "thinking_stone_bricks", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICK_SLAB, "thinking_stone_brick_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICK_STAIRS, "thinking_stone_brick_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILES, "thinking_stone_tiles", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILE_SLAB, "thinking_stone_tile_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILE_STAIRS, "thinking_stone_tile_stairs", HOTM_BUILDING_ITEM_SETTINGS)
    }

    private fun register(block: Block, name: String, itemSettings: Item.Settings) {
        val identifier = HotMConstants.identifier(name)
        Registry.register(Registry.BLOCK, identifier, block)
        Registry.register(Registry.ITEM, identifier, BlockItem(block, itemSettings))
    }

    private fun mainGroupItem(): ItemStack {
        return ItemStack(CYAN_THINKING_STONE_LAMP)
    }
}