package com.github.hotm.mod.block

import com.github.hotm.mod.Constants
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings

object HotMBlocks {
    val BLOCK_ITEM_SETTINGS by lazy { QuiltItemSettings() }

    val STONE_SETTINGS by lazy {
        QuiltBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).requiresTool().strength(3.0f, 10.0f)
            .sounds(BlockSoundGroup.STONE)
    }
    val THINKING_SCRAP_SETTINGS by lazy {
        QuiltBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(2.0f, 5.0f)
            .sounds(BlockSoundGroup.METAL)
    }

    val THINKING_STONE by lazy { Block(STONE_SETTINGS) }

    val THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val RUSTED_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }
    val PLASSEIN_THINKING_SCRAP by lazy { Block(THINKING_SCRAP_SETTINGS) }

    fun init() {
        register(THINKING_STONE, "thinking_stone")
        register(THINKING_SCRAP, "thinking_scrap")
        register(RUSTED_THINKING_SCRAP, "rusted_thinking_scrap")
        register(PLASSEIN_THINKING_SCRAP, "plassein_thinking_scrap")
    }

    private fun register(block: Block, path: String) {
        val blockItem = BlockItem(block, BLOCK_ITEM_SETTINGS)
        Registry.register(Registries.BLOCK, Constants.id(path), block)
        Registry.register(Registries.ITEM, Constants.id(path), blockItem)
    }
}
