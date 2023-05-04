package com.github.hotm.mod.client

import com.github.hotm.mod.block.HotMBlocks
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object HotMBlocksClient {
    fun init() {
        BlockRenderLayerMap.put(RenderLayer.getCutout(), HotMBlocks.PLASSEIN_THINKING_SCRAP)
    }
}
