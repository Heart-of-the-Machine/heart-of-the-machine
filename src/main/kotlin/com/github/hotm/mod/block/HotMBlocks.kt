package com.github.hotm.mod.block

import com.github.hotm.mod.Constants
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import net.minecraft.block.*
import net.minecraft.block.enums.NoteBlockInstrument
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

    val THINKING_STONE by lazy { Block(STONE_SETTINGS) }

    val THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val RUSTED_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val PLASSEIN_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }

    private val SAND_SETTINGS by lazy {
        QuiltBlockSettings.create().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.SNARE).strength(0.5f)
            .sounds(BlockSoundGroup.SAND)
    }
    val THINKING_SAND by lazy { SandBlock(0x29261d, SAND_SETTINGS) }

    // Leylines

    val THINKING_STONE_LEYLINE by lazy { Block(STONE_SETTINGS) }
    val THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val RUSTED_THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val PLASSEIN_THINKING_SCRAP_LEYLINE by lazy { Block(THINKING_SCRAP_SETTINGS) }

    fun init() {
        register(THINKING_STONE, "thinking_stone")
        register(THINKING_SCRAP, "thinking_scrap")
        register(RUSTED_THINKING_SCRAP, "rusted_thinking_scrap")
        register(PLASSEIN_THINKING_SCRAP, "plassein_thinking_scrap")

        register(THINKING_SAND, "thinking_sand")

        register(THINKING_STONE_LEYLINE, "thinking_stone_leyline")
        register(THINKING_SCRAP_LEYLINE, "thinking_scrap_leyline")
        register(RUSTED_THINKING_SCRAP_LEYLINE, "rusted_thinking_scrap_leyline")
        register(PLASSEIN_THINKING_SCRAP_LEYLINE, "plassein_thinking_scrap_leyline")
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
