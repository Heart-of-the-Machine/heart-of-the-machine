package com.github.hotm.client

import com.github.hotm.HotMBlocks
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.world.BiomeColors

object HotMColorProviders {
    fun register() {
        ColorProviderRegistry.BLOCK.register(
            { _, view, pos, _ ->
                if (view != null && pos != null) {
                    BiomeColors.getGrassColor(view, pos)
                } else {
                    0x4287f5
                }
            },
            HotMBlocks.MACHINE_CASING_LEYLINE,
            HotMBlocks.PLASSEIN_GRASS,
            HotMBlocks.PLASSEIN_STEM_LEYLINE,
            HotMBlocks.RUSTED_MACHINE_CASING_LEYLINE,
            HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE,
            HotMBlocks.SURFACE_MACHINE_CASING_LEYLINE,
            HotMBlocks.THINKING_STONE_LEYLINE
        )
        ColorProviderRegistry.ITEM.register(
            { _, _ -> 0x4287f5 },
            HotMBlocks.MACHINE_CASING_LEYLINE.asItem(),
            HotMBlocks.PLASSEIN_GRASS.asItem(),
            HotMBlocks.PLASSEIN_STEM_LEYLINE.asItem(),
            HotMBlocks.RUSTED_MACHINE_CASING_LEYLINE.asItem(),
            HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE.asItem(),
            HotMBlocks.SURFACE_MACHINE_CASING_LEYLINE.asItem(),
            HotMBlocks.THINKING_STONE_LEYLINE.asItem()
        )
    }
}