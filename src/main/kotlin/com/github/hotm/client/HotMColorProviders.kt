package com.github.hotm.client

import com.github.hotm.HotMBlocks
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.world.BiomeColors
import net.minecraft.client.color.world.GrassColors

object HotMColorProviders {
    fun register() {
        ColorProviderRegistry.BLOCK.register({ _, view, pos, _ ->
            if (view != null && pos != null) {
                BiomeColors.getGrassColor(view, pos)
            } else {
                GrassColors.getColor(0.5, 1.0)
            }
        }, HotMBlocks.PLASSEIN_GRASS)
        ColorProviderRegistry.ITEM.register(
            { _, _ -> GrassColors.getColor(0.5, 1.0) },
            HotMBlocks.PLASSEIN_GRASS.asItem()
        )
    }
}