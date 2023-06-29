package com.github.hotm.mod.client

import com.github.hotm.mod.block.HotMBlocks.NECTERE_PORTAL
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object HotMBlocksClient {
    fun init() {
        BlockRenderLayerMap.put(
            RenderLayer.getCutoutMipped(),
            PLASSEIN_THINKING_SCRAP
        )

        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), NECTERE_PORTAL)
    }
}
