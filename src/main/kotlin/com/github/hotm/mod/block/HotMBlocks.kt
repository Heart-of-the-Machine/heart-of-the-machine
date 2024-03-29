package com.github.hotm.mod.block

import com.github.hotm.mod.Constants
import com.github.hotm.mod.block.sprout.SolarArraySproutGenerator
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.GlassBlock
import net.minecraft.block.MapColor
import net.minecraft.block.PillarBlock
import net.minecraft.block.SandBlock
import net.minecraft.block.SlabBlock
import net.minecraft.block.StairsBlock
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

object HotMBlocks {
    private val BLOCK_ITEM_SETTINGS by lazy { QuiltItemSettings() }

    private val STONE_SETTINGS by lazy {
        QuiltBlockSettings.create().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool()
            .strength(3.0f, 10.0f).sounds(BlockSoundGroup.STONE)
    }
    private val THINKING_SCRAP_SETTINGS by lazy {
        QuiltBlockSettings.create().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresTool().strength(2.0f, 5.0f).sounds(BlockSoundGroup.METAL)
    }
    private val CRYSTAL_SETTINS by lazy {
        QuiltBlockSettings.create().instrument(NoteBlockInstrument.PLING).strength(0.3f).sounds(BlockSoundGroup.GLASS)
            .luminance { 15 }.solidBlock(::never)
    }
    private val LAMP_SETTINGS by lazy {
        QuiltBlockSettings.create().luminance { 15 }.strength(0.3f).sounds(BlockSoundGroup.GLASS)
    }

    val THINKING_STONE by lazy { Block(STONE_SETTINGS) }

    val THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val RUSTED_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val PLASSEIN_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }

    val SMOOTH_THINKING_STONE by lazy { Block(STONE_SETTINGS) }
    val THINKING_STONE_BRICKS by lazy { Block(STONE_SETTINGS) }
    val THINKING_STONE_TILES by lazy { Block(STONE_SETTINGS) }
    val SMOOTH_THINKING_STONE_STAIRS by lazy { StairsBlock(SMOOTH_THINKING_STONE.defaultState, STONE_SETTINGS) }
    val THINKING_STONE_BRICK_STAIRS by lazy { StairsBlock(THINKING_STONE_BRICKS.defaultState, STONE_SETTINGS) }
    val THINKING_STONE_TILE_STAIRS by lazy { StairsBlock(THINKING_STONE_TILES.defaultState, STONE_SETTINGS) }
    val SMOOTH_THINKING_STONE_SLAB by lazy { SlabBlock(STONE_SETTINGS) }
    val THINKING_STONE_BRICK_SLAB by lazy { SlabBlock(STONE_SETTINGS) }
    val THINKING_STONE_TILE_SLAB by lazy { SlabBlock(STONE_SETTINGS) }

    val OBELISK_PART by lazy { PillarBlock(STONE_SETTINGS) }
    val GLOWY_OBELISK_PART by lazy { PillarBlock(STONE_SETTINGS) }

    private val SAND_SETTINGS by lazy {
        QuiltBlockSettings.create().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.SNARE).strength(0.5f)
            .sounds(BlockSoundGroup.SAND)
    }
    val THINKING_SAND by lazy { SandBlock(0x29261d, SAND_SETTINGS) }
    val THINKING_GLASS by lazy {
        GlassBlock(
            QuiltBlockSettings.create().instrument(NoteBlockInstrument.HAT).strength(0.3f).sounds(BlockSoundGroup.GLASS)
                .nonOpaque().allowsSpawning(::never).solidBlock(::never).suffocates(::never).blockVision(::never)
        )
    }

    val AURA_CRYSTAL by lazy { Block(QuiltBlockSettings.copyOf(CRYSTAL_SETTINS).mapColor(MapColor.CYAN)) }
    val HOLO_CRYSTAL by lazy { Block(QuiltBlockSettings.copyOf(CRYSTAL_SETTINS).mapColor(MapColor.MAGENTA)) }
    val AURA_LAMP by lazy { Block(QuiltBlockSettings.copyOf(LAMP_SETTINGS).mapColor(MapColor.CYAN)) }
    val HOLO_LAMP by lazy { Block(QuiltBlockSettings.copyOf(LAMP_SETTINGS).mapColor(MapColor.MAGENTA)) }
    val AURA_THINKING_STONE by lazy {
        PillarBlock(QuiltBlockSettings.copyOf(LAMP_SETTINGS).mapColor(MapColor.CYAN))
    }
    val HOLO_THINKING_STONE by lazy {
        PillarBlock(QuiltBlockSettings.copyOf(LAMP_SETTINGS).mapColor(MapColor.MAGENTA))
    }

    // Plants

    private val WOOD_SETTINGS =
        QuiltBlockSettings.create().mapColor(MapColor.BLUE).instrument(NoteBlockInstrument.BASS).strength(2.0f)
            .sounds(BlockSoundGroup.WOOD).lavaIgnitable()
    private val LEAVES_SETTINGS =
        QuiltBlockSettings.create().mapColor(MapColor.LIGHT_BLUE).strength(0.2f).ticksRandomly()
            .sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(HotMBlocks::never).suffocates(HotMBlocks::never)
            .blockVision(HotMBlocks::never).lavaIgnitable().pistonBehavior(PistonBehavior.DESTROY)
            .solidBlock(HotMBlocks::never)
    private val SPROUT_SETTINGS =
        QuiltBlockSettings.create().mapColor(MapColor.PLANT).noCollision().ticksRandomly().breakInstantly()
            .sounds(BlockSoundGroup.GRASS).pistonBehavior(PistonBehavior.DESTROY)
    private val PLANT_SETTINGS =
        QuiltBlockSettings.create().mapColor(MapColor.PLANT).noCollision().breakInstantly()
            .sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XZ).lavaIgnitable()
            .pistonBehavior(PistonBehavior.DESTROY)
    val SOLAR_ARRAY_STEM by lazy { PillarBlock(WOOD_SETTINGS) }
    val SOLAR_ARRAY_LEAVES by lazy { PlasseinLeavesBlock(LEAVES_SETTINGS) }
    val SOLAR_ARRAY_SPROUT by lazy { PlasseinSproutBlock(SolarArraySproutGenerator, SPROUT_SETTINGS) }

    val SPOROFRUIT by lazy { PlasseinFlowerBlock(PLANT_SETTINGS) }

    // Leylines

    val THINKING_STONE_LEYLINE by lazy { Block(STONE_SETTINGS) }
    val THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val RUSTED_THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val PLASSEIN_THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val SMOOTH_THINKING_STONE_LEYLINE by lazy { Block(STONE_SETTINGS) }

    // Portal

    val NECTERE_PORTAL by lazy {
        NecterePortalBlock(
            QuiltBlockSettings.create().mapColor(MapColor.CYAN).strength(-1.0f)
                .sounds(BlockSoundGroup.GLASS).nonOpaque().luminance { 12 }.pistonBehavior(PistonBehavior.BLOCK)
        )
    }
    val NECTERE_PORTAL_SPAWNER by lazy {
        NecterePortalSpawnerBlock(
            QuiltBlockSettings.create().mapColor(MapColor.CYAN).strength(-1.0f).sounds(BlockSoundGroup.STONE)
                .pistonBehavior(PistonBehavior.BLOCK)
        )
    }

    fun init() {
        register(THINKING_STONE, "thinking_stone")
        register(THINKING_SCRAP, "thinking_scrap")
        register(RUSTED_THINKING_SCRAP, "rusted_thinking_scrap")
        register(PLASSEIN_THINKING_SCRAP, "plassein_thinking_scrap")
        register(SMOOTH_THINKING_STONE, "smooth_thinking_stone")
        register(THINKING_STONE_BRICKS, "thinking_stone_bricks")
        register(THINKING_STONE_TILES, "thinking_stone_tiles")
        register(SMOOTH_THINKING_STONE_STAIRS, "smooth_thinking_stone_stairs")
        register(THINKING_STONE_BRICK_STAIRS, "thinking_stone_brick_stairs")
        register(THINKING_STONE_TILE_STAIRS, "thinking_stone_tile_stairs")
        register(SMOOTH_THINKING_STONE_SLAB, "smooth_thinking_stone_slab")
        register(THINKING_STONE_BRICK_SLAB, "thinking_stone_brick_slab")
        register(THINKING_STONE_TILE_SLAB, "thinking_stone_tile_slab")

        register(OBELISK_PART, "obelisk_part")
        register(GLOWY_OBELISK_PART, "glowy_obelisk_part")

        register(THINKING_SAND, "thinking_sand")
        register(THINKING_GLASS, "thinking_glass")

        register(AURA_CRYSTAL, "aura_crystal")
        register(HOLO_CRYSTAL, "holo_crystal")
        register(AURA_LAMP, "aura_lamp")
        register(HOLO_LAMP, "holo_lamp")
        register(AURA_THINKING_STONE, "aura_thinking_stone")
        register(HOLO_THINKING_STONE, "holo_thinking_stone")

        register(SOLAR_ARRAY_STEM, "solar_array_stem")
        register(SOLAR_ARRAY_LEAVES, "solar_array_leaves")
        register(SOLAR_ARRAY_SPROUT, "solar_array_sprout")

        register(SPOROFRUIT, "sporofruit")

        register(THINKING_STONE_LEYLINE, "thinking_stone_leyline")
        register(THINKING_SCRAP_LEYLINE, "thinking_scrap_leyline")
        register(RUSTED_THINKING_SCRAP_LEYLINE, "rusted_thinking_scrap_leyline")
        register(PLASSEIN_THINKING_SCRAP_LEYLINE, "plassein_thinking_scrap_leyline")
        register(SMOOTH_THINKING_STONE_LEYLINE, "smooth_thinking_stone_leyline")

        register(NECTERE_PORTAL, "nectere_portal")
        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner")
    }

    private fun register(block: Block, path: String) {
        val blockItem = BlockItem(block, BLOCK_ITEM_SETTINGS)
        Registry.register(Registries.BLOCK, Constants.id(path), block)
        Registry.register(Registries.ITEM, Constants.id(path), blockItem)
    }

    @Suppress("unused_parameter")
    private fun never(state: BlockState, view: BlockView, pos: BlockPos): Boolean = false

    @Suppress("unused_parameter")
    private fun <T> never(state: BlockState, view: BlockView, pos: BlockPos, t: T): Boolean = false
}
