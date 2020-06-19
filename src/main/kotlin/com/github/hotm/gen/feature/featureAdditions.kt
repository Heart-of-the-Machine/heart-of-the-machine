package com.github.hotm.gen.feature

import com.github.hotm.gen.HotMSurfaceConfigs
import net.minecraft.block.Block
import net.minecraft.world.gen.feature.Feature

/**
 * Used by HotM features to tell if they're generating on the surface.
 */
fun Feature<*>.isSurface(block: Block): Boolean {
    return block == HotMSurfaceConfigs.SURFACE_BLOCK || block == HotMSurfaceConfigs.SUBSURFACE_BLOCK
}
