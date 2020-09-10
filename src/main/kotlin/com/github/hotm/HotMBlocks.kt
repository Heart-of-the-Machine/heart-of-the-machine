package com.github.hotm

import com.github.hotm.HotMItems.HOTM_BUILDING_ITEM_SETTINGS
import com.github.hotm.blocks.NecterePortalBlock
import com.github.hotm.blocks.NecterePortalSpawnerBlock
import com.github.hotm.mixinapi.BlockCreators
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.*
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView

/**
 * Static block access and initialization.
 */
object HotMBlocks {

    /*
     * Crystals & Lamps.
     */
    private val CRYSTAL_BLOCK_SETTINGS =
        FabricBlockSettings.of(Material.GLASS).requiresTool().breakByTool(FabricToolTags.PICKAXES).strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.GLASS).lightLevel(15)
    private val LAMP_BLOCK_SETTINGS =
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.STONE)
            .lightLevel(15)
    private val MACHINE_CASING_LAMP_BLOCK_SETTINGS =
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.METAL)
            .lightLevel(15)
    val CYAN_CRYSTAL = Block(CRYSTAL_BLOCK_SETTINGS)
    val CYAN_CRYSTAL_LAMP = Block(LAMP_BLOCK_SETTINGS)
    val CYAN_MACHINE_CASING_LAMP = Block(MACHINE_CASING_LAMP_BLOCK_SETTINGS)
    val CYAN_THINKING_STONE_LAMP = Block(LAMP_BLOCK_SETTINGS)
    val MAGENTA_CRYSTAL = Block(CRYSTAL_BLOCK_SETTINGS)
    val MAGENTA_CRYSTAL_LAMP = Block(LAMP_BLOCK_SETTINGS)
    val MAGENTA_MACHINE_CASING_LAMP = Block(MACHINE_CASING_LAMP_BLOCK_SETTINGS)
    val MAGENTA_THINKING_STONE_LAMP = Block(LAMP_BLOCK_SETTINGS)

    /*
     * Obelisk parts.
     */
    private val OBELISK_PART_SETTINGS =
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE)
    val GLOWY_OBELISK_PART = PillarBlock(OBELISK_PART_SETTINGS)
    val OBELISK_PART = PillarBlock(OBELISK_PART_SETTINGS)
    val NECTERE_PORTAL = NecterePortalBlock(
        FabricBlockSettings.of(Material.PORTAL).noCollision().strength(-1.0f).sounds(BlockSoundGroup.GLASS).nonOpaque()
            .lightLevel { 12 })
    val NECTERE_PORTAL_SPAWNER = NecterePortalSpawnerBlock(FabricBlockSettings.of(Material.STONE).strength(-1.0f))

    /*
     * Machine Casing Blocks.
     */
    private val MACHINE_CASING_SETTINGS =
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.METAL)
    val MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val MACHINE_CASING_BRICKS = Block(MACHINE_CASING_SETTINGS)
    val METAL_MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val PLASSEIN_MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val RUSTED_MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val SURFACE_MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val TEST_MACHINE_CASING = Block(MACHINE_CASING_SETTINGS)
    val MACHINE_CASING_STAIRS = BlockCreators.createStairsBlock(MACHINE_CASING.defaultState, MACHINE_CASING_SETTINGS)
    val MACHINE_CASING_BRICK_STAIRS =
        BlockCreators.createStairsBlock(MACHINE_CASING_BRICKS.defaultState, MACHINE_CASING_SETTINGS)
    val MACHINE_CASING_SLAB = SlabBlock(MACHINE_CASING_SETTINGS)
    val MACHINE_CASING_BRICK_SLAB = SlabBlock(MACHINE_CASING_SETTINGS)

    /*
     * Plassein Growth Blocks.
     */
    val PLASSEIN_STEM_SETTINGS =
        FabricBlockSettings.of(Material.WOOD).breakByTool(FabricToolTags.AXES).strength(1.0f, 10.0f)
            .sounds(BlockSoundGroup.WOOD)
    val PLASSEIN_BLOOM = Block(
        FabricBlockSettings.of(Material.LEAVES).strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOL)
            .nonOpaque().allowsSpawning(HotMBlocks::never)
    )
    val PLASSEIN_GRASS = Block(MACHINE_CASING_SETTINGS)
    val PLASSEIN_LEAVES = Block(
        FabricBlockSettings.of(Material.LEAVES).strength(0.2f).sounds(BlockSoundGroup.GRASS).nonOpaque()
            .allowsSpawning(HotMBlocks::never).suffocates(HotMBlocks::never).blockVision(HotMBlocks::never)
    )
    val PLASSEIN_STEM = PillarBlock(PLASSEIN_STEM_SETTINGS)

    /*
     * Thinking Stone Blocks.
     */
    private val THINKING_STONE_SETTINGS =
        FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE)
    val SMOOTH_THINKING_STONE = Block(THINKING_STONE_SETTINGS)
    val THINKING_STONE = Block(THINKING_STONE_SETTINGS)
    val THINKING_STONE_BRICKS = Block(THINKING_STONE_SETTINGS)
    val THINKING_STONE_TILES = Block(THINKING_STONE_SETTINGS)
    val SMOOTH_THINKING_STONE_STAIRS =
        BlockCreators.createStairsBlock(SMOOTH_THINKING_STONE.defaultState, THINKING_STONE_SETTINGS)
    val THINKING_STONE_STAIRS = BlockCreators.createStairsBlock(THINKING_STONE.defaultState, THINKING_STONE_SETTINGS)
    val THINKING_STONE_BRICK_STAIRS =
        BlockCreators.createStairsBlock(THINKING_STONE_BRICKS.defaultState, THINKING_STONE_SETTINGS)
    val THINKING_STONE_TILE_STAIRS =
        BlockCreators.createStairsBlock(THINKING_STONE_TILES.defaultState, THINKING_STONE_SETTINGS)
    val SMOOTH_THINKING_STONE_SLAB = SlabBlock(THINKING_STONE_SETTINGS)
    val THINKING_STONE_SLAB = SlabBlock(THINKING_STONE_SETTINGS)
    val THINKING_STONE_BRICK_SLAB = SlabBlock(THINKING_STONE_SETTINGS)
    val THINKING_STONE_TILE_SLAB = SlabBlock(THINKING_STONE_SETTINGS)

    /*
     * Thinking glass blocks.
     */
    private val GLASS_SETTINGS =
        FabricBlockSettings.of(Material.GLASS).strength(0.5f, 10.0f).sounds(BlockSoundGroup.GLASS).nonOpaque()
            .allowsSpawning(HotMBlocks::never).solidBlock(HotMBlocks::never).suffocates(HotMBlocks::never)
            .blockVision(HotMBlocks::never)
    val THINKING_GLASS = GlassBlock(GLASS_SETTINGS)

    /*
     * Leyline blocks.
     */
    val MACHINE_CASING_LEYLINE = Block(MACHINE_CASING_SETTINGS)
    val PLASSEIN_GRASS_LEYLINE = Block(MACHINE_CASING_SETTINGS)
    val PLASSEIN_STEM_LEYLINE = PillarBlock(PLASSEIN_STEM_SETTINGS)
    val RUSTED_MACHINE_CASING_LEYLINE = Block(MACHINE_CASING_SETTINGS)
    val SMOOTH_THINKING_STONE_LEYLINE = Block(THINKING_STONE_SETTINGS)
    val SURFACE_MACHINE_CASING_LEYLINE = Block(MACHINE_CASING_SETTINGS)
    val THINKING_STONE_LEYLINE = Block(THINKING_STONE_SETTINGS)

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
        register(MACHINE_CASING_LEYLINE, "machine_casing_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_CRYSTAL, "magenta_crystal", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_CRYSTAL_LAMP, "magenta_crystal_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_MACHINE_CASING_LAMP, "magenta_machine_casing_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(MAGENTA_THINKING_STONE_LAMP, "magenta_thinking_stone_lamp", HOTM_BUILDING_ITEM_SETTINGS)
        register(METAL_MACHINE_CASING, "metal_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(NECTERE_PORTAL, "nectere_portal", Item.Settings())
        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner", Item.Settings())
        register(OBELISK_PART, "obelisk_part", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_BLOOM, "plassein_bloom", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_GRASS, "plassein_grass", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_GRASS_LEYLINE, "plassein_grass_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_LEAVES, "plassein_leaves", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_MACHINE_CASING, "plassein_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_STEM, "plassein_stem", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_STEM_LEYLINE, "plassein_stem_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(RUSTED_MACHINE_CASING, "rusted_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(RUSTED_MACHINE_CASING_LEYLINE, "rusted_machine_casing_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE, "smooth_thinking_stone", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE_LEYLINE, "smooth_thinking_stone_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE_SLAB, "smooth_thinking_stone_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(SMOOTH_THINKING_STONE_STAIRS, "smooth_thinking_stone_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(SURFACE_MACHINE_CASING, "surface_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(SURFACE_MACHINE_CASING_LEYLINE, "surface_machine_casing_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(TEST_MACHINE_CASING, "test_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_GLASS, "thinking_glass", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE, "thinking_stone", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_SLAB, "thinking_stone_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_STAIRS, "thinking_stone_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICKS, "thinking_stone_bricks", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICK_SLAB, "thinking_stone_brick_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_BRICK_STAIRS, "thinking_stone_brick_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILES, "thinking_stone_tiles", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILE_SLAB, "thinking_stone_tile_slab", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_TILE_STAIRS, "thinking_stone_tile_stairs", HOTM_BUILDING_ITEM_SETTINGS)
        register(THINKING_STONE_LEYLINE, "thinking_stone_leyline", HOTM_BUILDING_ITEM_SETTINGS)
    }

    private fun register(block: Block, name: String, itemSettings: Item.Settings) {
        val identifier = HotMConstants.identifier(name)
        Registry.register(Registry.BLOCK, identifier, block)
        Registry.register(Registry.ITEM, identifier, BlockItem(block, itemSettings))
    }

    private fun never(state: BlockState, world: BlockView, pos: BlockPos, entity: EntityType<*>): Boolean {
        return false
    }

    private fun never(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return false
    }
}