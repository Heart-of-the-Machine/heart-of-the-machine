package com.github.hotm.blocks

import com.github.hotm.HotMConstants
import com.github.hotm.blocks.spore.StandardPlasseinSporeGenerator
import com.github.hotm.items.BracingItem
import com.github.hotm.items.HotMItems.HOTM_BUILDING_ITEM_SETTINGS
import com.github.hotm.items.HotMItems.HOTM_MATERIAL_ITEM_SETTINGS
import com.github.hotm.items.ScaffoldingItem
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
     * Block Settings.
     */
    private val BRACING_SETTINGS by lazy {
        FabricBlockSettings.of(Material.METAL, MapColor.GRAY).requiresTool().strength(1.0f, 15.0f)
            .sounds(BlockSoundGroup.METAL).nonOpaque()
    }
    private val CYAN_CRYSTAL_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.GLASS, MapColor.CYAN).requiresTool().breakByTool(FabricToolTags.PICKAXES)
            .strength(2.0f, 5.0f).sounds(BlockSoundGroup.GLASS).luminance(15)
    }
    private val CYAN_LAMP_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.STONE, MapColor.CYAN).requiresTool().strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.STONE).luminance(15)
    }
    private val CYAN_MACHINE_CASING_LAMP_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.METAL, MapColor.CYAN).requiresTool().strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.METAL).luminance(15)
    }
    private val MACHINE_CASING_SETTINGS by lazy {
        FabricBlockSettings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(3.0f, 10.0f)
            .sounds(BlockSoundGroup.METAL)
    }
    private val MAGENTA_CRYSTAL_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.GLASS, MapColor.MAGENTA).requiresTool().breakByTool(FabricToolTags.PICKAXES)
            .strength(2.0f, 5.0f).sounds(BlockSoundGroup.GLASS).luminance(15)
    }
    private val MAGENTA_LAMP_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.STONE, MapColor.MAGENTA).requiresTool().strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.STONE).luminance(15)
    }
    private val MAGENTA_MACHINE_CASING_LAMP_BLOCK_SETTINGS by lazy {
        FabricBlockSettings.of(Material.METAL, MapColor.MAGENTA).requiresTool().strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.METAL).luminance(15)
    }
    private val OBELISK_PART_SETTINGS by lazy {
        FabricBlockSettings.of(Material.METAL, MapColor.DARK_AQUA).requiresTool().strength(3.0f, 10.0f)
            .sounds(BlockSoundGroup.STONE)
    }
    private val PLASSEIN_LOG_SETTINGS by lazy {
        FabricBlockSettings.of(Material.WOOD, MapColor.BLUE).breakByTool(FabricToolTags.AXES).strength(1.0f, 10.0f)
            .sounds(BlockSoundGroup.WOOD)
    }
    private val THINKING_STONE_SETTINGS by lazy {
        FabricBlockSettings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(3.0f, 10.0f)
            .sounds(BlockSoundGroup.STONE)
    }


    /*
     * Leyline blocks.
     */
    private val LEYLINE_BLOCKS = hashSetOf<Block>()
    val MACHINE_CASING_LEYLINE by lazy { addLeyline(Block(MACHINE_CASING_SETTINGS)) }
    val PLASSEIN_GRASS_LEYLINE by lazy { addLeyline(Block(MACHINE_CASING_SETTINGS)) }
    val PLASSEIN_LOG_LEYLINE by lazy { addLeyline(PillarBlock(PLASSEIN_LOG_SETTINGS)) }
    val RUSTED_MACHINE_CASING_LEYLINE by lazy { addLeyline(Block(MACHINE_CASING_SETTINGS)) }
    val SMOOTH_THINKING_STONE_LEYLINE by lazy { addLeyline(Block(THINKING_STONE_SETTINGS)) }
    val SURFACE_MACHINE_CASING_LEYLINE by lazy { addLeyline(Block(MACHINE_CASING_SETTINGS)) }
    val THINKING_STONE_LEYLINE by lazy { addLeyline(Block(THINKING_STONE_SETTINGS)) }

    /*
     * Crystals & Lamps.
     */
    val CYAN_CRYSTAL by lazy { Block(CYAN_CRYSTAL_BLOCK_SETTINGS) }
    val CYAN_CRYSTAL_LAMP by lazy { Block(CYAN_LAMP_BLOCK_SETTINGS) }
    val CYAN_MACHINE_CASING_LAMP by lazy { Block(CYAN_MACHINE_CASING_LAMP_BLOCK_SETTINGS) }
    val CYAN_THINKING_STONE_LAMP by lazy { Block(CYAN_LAMP_BLOCK_SETTINGS) }
    val MAGENTA_CRYSTAL by lazy { Block(MAGENTA_CRYSTAL_BLOCK_SETTINGS) }
    val MAGENTA_CRYSTAL_LAMP by lazy { Block(MAGENTA_LAMP_BLOCK_SETTINGS) }
    val MAGENTA_MACHINE_CASING_LAMP by lazy { Block(MAGENTA_MACHINE_CASING_LAMP_BLOCK_SETTINGS) }
    val MAGENTA_THINKING_STONE_LAMP by lazy { Block(MAGENTA_LAMP_BLOCK_SETTINGS) }

    /*
     * Obelisk parts.
     */
    val GLOWY_OBELISK_PART by lazy { PillarBlock(OBELISK_PART_SETTINGS) }
    val OBELISK_PART by lazy { PillarBlock(OBELISK_PART_SETTINGS) }
    val NECTERE_PORTAL by lazy {
        NecterePortalBlock(
            FabricBlockSettings.of(Material.PORTAL, MapColor.CYAN).noCollision().strength(-1.0f)
                .sounds(BlockSoundGroup.GLASS).nonOpaque().luminance(12)
        )
    }
    val NECTERE_PORTAL_SPAWNER by lazy {
        NecterePortalSpawnerBlock(FabricBlockSettings.of(Material.STONE, MapColor.DARK_AQUA).strength(-1.0f))
    }

    /*
     * Machine Casing Blocks.
     */
    val MACHINE_CASING by lazy { LeylineableBlock(MACHINE_CASING_LEYLINE, MACHINE_CASING_SETTINGS) }
    val MACHINE_CASING_BRICKS by lazy { Block(MACHINE_CASING_SETTINGS) }
    val METAL_MACHINE_CASING by lazy { Block(MACHINE_CASING_SETTINGS) }
    val PLASSEIN_MACHINE_CASING by lazy { Block(MACHINE_CASING_SETTINGS) }
    val RUSTED_MACHINE_CASING by lazy { LeylineableBlock(RUSTED_MACHINE_CASING_LEYLINE, MACHINE_CASING_SETTINGS) }
    val SURFACE_MACHINE_CASING by lazy { LeylineableBlock(SURFACE_MACHINE_CASING_LEYLINE, MACHINE_CASING_SETTINGS) }
    val TEST_MACHINE_CASING by lazy { Block(MACHINE_CASING_SETTINGS) }
    val MACHINE_CASING_STAIRS by lazy {
        BlockCreators.createStairsBlock(MACHINE_CASING.defaultState, MACHINE_CASING_SETTINGS)
    }
    val MACHINE_CASING_BRICK_STAIRS by lazy {
        BlockCreators.createStairsBlock(MACHINE_CASING_BRICKS.defaultState, MACHINE_CASING_SETTINGS)
    }
    val MACHINE_CASING_SLAB by lazy { SlabBlock(MACHINE_CASING_SETTINGS) }
    val MACHINE_CASING_BRICK_SLAB by lazy { SlabBlock(MACHINE_CASING_SETTINGS) }

    /*
     * Bracing Blocks.
     */
    val METAL_BRACING by lazy { BracingBlock(BRACING_SETTINGS) }
    val PLASSEIN_BRACING by lazy { BracingBlock(BRACING_SETTINGS) }

    /*
     * Plassein Blocks.
     */
    val PLASSEIN_BLOOM by lazy {
        Block(
            FabricBlockSettings.of(Material.LEAVES, MapColor.BLUE).strength(1.0f, 10.0f).sounds(BlockSoundGroup.WOOL)
                .nonOpaque().allowsSpawning(HotMBlocks::never)
        )
    }
    val PLASSEIN_FUEL_BLOCK by lazy {
        Block(
            FabricBlockSettings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(5.0F, 6.0F)
        )
    }
    val PLASSEIN_GRASS by lazy { LeylineableBlock(PLASSEIN_GRASS_LEYLINE, MACHINE_CASING_SETTINGS) }
    val PLASSEIN_LEAVES by lazy {
        PlasseinLeavesBlock(
            FabricBlockSettings.of(Material.LEAVES, MapColor.CYAN).strength(0.2f).ticksRandomly()
                .sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(HotMBlocks::never)
                .suffocates(HotMBlocks::never).blockVision(HotMBlocks::never)
        )
    }
    val PLASSEIN_LOG by lazy { LeylineablePillarBlock(PLASSEIN_LOG_LEYLINE, PLASSEIN_LOG_SETTINGS) }
    val PLASSEIN_PLANKS by lazy { Block(PLASSEIN_LOG_SETTINGS) }
    val PLASSEIN_SCAFFOLDING by lazy {
        ScaffoldingBlock(
            FabricBlockSettings.of(Material.DECORATION, MapColor.BLUE).noCollision().sounds(BlockSoundGroup.SCAFFOLDING)
                .dynamicBounds()
        )
    }
    val PLASSEIN_SPORE by lazy {
        PlasseinSporeBlock(
            StandardPlasseinSporeGenerator,
            FabricBlockSettings.of(Material.PLANT, MapColor.BLUE).noCollision().ticksRandomly().breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
        )
    }
    val PLASSEIN_STEM by lazy { PillarBlock(PLASSEIN_LOG_SETTINGS) }

    /*
     * Thinking Stone Blocks.
     */
    val SMOOTH_THINKING_STONE by lazy { LeylineableBlock(SMOOTH_THINKING_STONE_LEYLINE, THINKING_STONE_SETTINGS) }
    val THINKING_STONE by lazy { LeylineableBlock(THINKING_STONE_LEYLINE, THINKING_STONE_SETTINGS) }
    val THINKING_STONE_BRICKS by lazy { Block(THINKING_STONE_SETTINGS) }
    val THINKING_STONE_TILES by lazy { Block(THINKING_STONE_SETTINGS) }
    val SMOOTH_THINKING_STONE_STAIRS by lazy {
        BlockCreators.createStairsBlock(SMOOTH_THINKING_STONE.defaultState, THINKING_STONE_SETTINGS)
    }
    val THINKING_STONE_STAIRS by lazy {
        BlockCreators.createStairsBlock(THINKING_STONE.defaultState, THINKING_STONE_SETTINGS)
    }
    val THINKING_STONE_BRICK_STAIRS by lazy {
        BlockCreators.createStairsBlock(THINKING_STONE_BRICKS.defaultState, THINKING_STONE_SETTINGS)
    }
    val THINKING_STONE_TILE_STAIRS by lazy {
        BlockCreators.createStairsBlock(THINKING_STONE_TILES.defaultState, THINKING_STONE_SETTINGS)
    }
    val SMOOTH_THINKING_STONE_SLAB by lazy { SlabBlock(THINKING_STONE_SETTINGS) }
    val THINKING_STONE_SLAB by lazy { SlabBlock(THINKING_STONE_SETTINGS) }
    val THINKING_STONE_BRICK_SLAB by lazy { SlabBlock(THINKING_STONE_SETTINGS) }
    val THINKING_STONE_TILE_SLAB by lazy { SlabBlock(THINKING_STONE_SETTINGS) }

    /*
     * Sand blocks.
     */
    private val SAND_SETTINGS by lazy {
        FabricBlockSettings.of(Material.AGGREGATE, MapColor.DEEPSLATE_GRAY).strength(0.5F).sounds(BlockSoundGroup.SAND)
    }
    val NULL_SAND by lazy { SandBlock(0x29261d, SAND_SETTINGS) }

    /*
     * Thinking glass blocks.
     */
    private val GLASS_SETTINGS by lazy {
        FabricBlockSettings.of(Material.GLASS, MapColor.BLACK).strength(0.5f, 10.0f).sounds(BlockSoundGroup.GLASS)
            .nonOpaque().allowsSpawning(HotMBlocks::never).solidBlock(HotMBlocks::never).suffocates(HotMBlocks::never)
            .blockVision(HotMBlocks::never)
    }
    val THINKING_GLASS by lazy { GlassBlock(GLASS_SETTINGS) }

    /*
     * Aura Node blocks.
     */
    val BASIC_SIPHON_AURA_NODE by lazy { BasicSiphonAuraNodeBlock(THINKING_STONE_SETTINGS) }
    val BASIC_SOURCE_AURA_NODE by lazy { BasicSourceAuraNodeBlock(THINKING_STONE_SETTINGS) }

    /**
     * Register all Heart of the Machine blocks...
     */
    fun register() {
        register(BASIC_SIPHON_AURA_NODE, "basic_siphon_aura_node", HOTM_BUILDING_ITEM_SETTINGS)
        register(BASIC_SOURCE_AURA_NODE, "basic_source_aura_node", HOTM_BUILDING_ITEM_SETTINGS)
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
        register(METAL_BRACING, "metal_bracing", BracingItem(METAL_BRACING, HOTM_BUILDING_ITEM_SETTINGS))
        register(METAL_MACHINE_CASING, "metal_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(NECTERE_PORTAL, "nectere_portal", Item.Settings())
        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner", Item.Settings())
        register(NULL_SAND, "null_sand", HOTM_BUILDING_ITEM_SETTINGS)
        register(OBELISK_PART, "obelisk_part", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_BLOOM, "plassein_bloom", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_BRACING, "plassein_bracing", BracingItem(PLASSEIN_BRACING, HOTM_BUILDING_ITEM_SETTINGS))
        register(PLASSEIN_FUEL_BLOCK, "plassein_fuel_block", HOTM_MATERIAL_ITEM_SETTINGS)
        register(PLASSEIN_GRASS, "plassein_grass", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_GRASS_LEYLINE, "plassein_grass_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_LEAVES, "plassein_leaves", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_LOG, "plassein_log", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_LOG_LEYLINE, "plassein_log_leyline", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_MACHINE_CASING, "plassein_machine_casing", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_PLANKS, "plassein_planks", HOTM_BUILDING_ITEM_SETTINGS)
        register(
            PLASSEIN_SCAFFOLDING,
            "plassein_scaffolding",
            ScaffoldingItem(PLASSEIN_SCAFFOLDING, HOTM_BUILDING_ITEM_SETTINGS)
        )
        register(PLASSEIN_SPORE, "plassein_spore", HOTM_BUILDING_ITEM_SETTINGS)
        register(PLASSEIN_STEM, "plassein_stem", HOTM_BUILDING_ITEM_SETTINGS)
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

    fun isLeyline(block: Block): Boolean {
        return LEYLINE_BLOCKS.contains(block)
    }

    private fun <B : Block> addLeyline(block: B): B {
        LEYLINE_BLOCKS.add(block)
        return block
    }

    private fun register(block: Block, name: String, itemSettings: Item.Settings) {
        register(block, name, BlockItem(block, itemSettings))
    }

    private fun register(block: Block, name: String, item: BlockItem) {
        val identifier = HotMConstants.identifier(name)
        Registry.register(Registry.BLOCK, identifier, block)
        Registry.register(Registry.ITEM, identifier, item)
    }

    private fun never(state: BlockState, world: BlockView, pos: BlockPos, entity: EntityType<*>): Boolean {
        return false
    }

    private fun never(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return false
    }
}