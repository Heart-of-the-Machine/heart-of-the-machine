package com.github.hotm.mod.client

import com.github.hotm.mod.block.HotMBlocks.NECTERE_PORTAL
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_LEAVES
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_SPROUT
import com.github.hotm.mod.block.HotMBlocks.SPOROFRUIT
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object HotMBlocksClient {
    fun init() {
        BlockRenderLayerMap.put(
            RenderLayer.getCutoutMipped(),
            PLASSEIN_THINKING_SCRAP,
            SOLAR_ARRAY_LEAVES,
            SOLAR_ARRAY_SPROUT,
            SPOROFRUIT
        )

        BlockRenderLayerMap.put(RenderLayer.getTranslucent(), NECTERE_PORTAL)
    }
}
