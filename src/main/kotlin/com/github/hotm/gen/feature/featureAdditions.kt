package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.gen.HotMSurfaceConfigs
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.world.gen.feature.Feature

/**
 * Used by HotM features to tell if they're generating on the surface.
 */
fun isSurface(block: Block): Boolean {
    return isNectereSurface(block) || Feature.isDirt(block) || isNectereStone(block) || isStone(block)
}

fun isStone(block: Block): Boolean {
    return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE
}

fun isNectereSurface(block: Block): Boolean {
    return block == HotMSurfaceConfigs.SURFACE_BLOCK || block == HotMSurfaceConfigs.SUBSURFACE_BLOCK
}

fun isNectereStone(block: Block): Boolean {
    return block == HotMBlocks.THINKING_STONE
}
