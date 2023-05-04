package com.github.hotm.mod.client

import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.block.BlockState
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.world.BiomeColors
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

object HotMColorProviders {
    fun init() {
        ColorProviderRegistry.BLOCK.register(Grass, PLASSEIN_THINKING_SCRAP)
    }

    private object Grass : BlockColorProvider {
        override fun getColor(
            state: BlockState, view: BlockRenderView?, pos: BlockPos?, tintIndex: Int
        ): Int {
            return if (view != null && pos != null) {
                BiomeColors.getGrassColor(view, pos)
            } else 0x4287f5
        }
    }
}
