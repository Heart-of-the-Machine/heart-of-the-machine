package com.github.hotm.world.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.world.gen.HotMSurfaceBuilders
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.gen.feature.Feature

/**
 * Utilities for when generating features.
 */
object FeatureUtils {

    /**
     * Used by HotM features to tell if they're generating on the surface.
     */
    fun isSurface(block: Block): Boolean {
        return isNectereSurface(block) || Feature.isSoil(block) || isNectereStone(block) || isStone(block)
    }

    fun isStone(block: Block): Boolean {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE
    }

    fun isNectereSurface(block: Block): Boolean {
        return block == HotMSurfaceBuilders.RUSTED_SURFACE_BLOCK || block == HotMSurfaceBuilders.SURFACE_BLOCK || block == HotMSurfaceBuilders.GRASS_BLOCK
    }

    fun isNectereStone(block: Block): Boolean {
        return block == HotMBlocks.THINKING_STONE
    }

    /**
     * Fills all blocks in a given area.
     */
    fun fillBlocks(
        world: ServerWorldAccess,
        minX: Int,
        minY: Int,
        minZ: Int,
        maxX: Int,
        maxY: Int,
        maxZ: Int,
        block: BlockState
    ) {
        val mutable = BlockPos.Mutable()
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    mutable.set(x, y, z)
                    world.setBlockState(mutable, block, 3)
                }
            }
        }
    }

    /**
     * Checks to see if this entire area is filled with air.
     */
    fun isFilledWithAir(
        world: ServerWorldAccess,
        minX: Int,
        minY: Int,
        minZ: Int,
        maxX: Int,
        maxY: Int,
        maxZ: Int
    ): Boolean {
        val mutable = BlockPos.Mutable()
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    mutable.set(x, y, z)
                    if (!world.isAir(mutable)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}
