package com.github.hotm.client

import com.github.hotm.blocks.HotMBlocks
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

/**
 * Handles client-side block operations like registering blocks as transparent.
 */
object HotMBlocksClient {

    /**
     * Handle client-side block operations.
     */
    fun register() {
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.METAL_BRACING, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_BLOOM, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_BRACING, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_GRASS, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_LEAVES, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_SCAFFOLDING, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.PLASSEIN_SPORE, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.NECTERE_PORTAL, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(HotMBlocks.THINKING_GLASS, RenderLayer.getCutout())
    }
}