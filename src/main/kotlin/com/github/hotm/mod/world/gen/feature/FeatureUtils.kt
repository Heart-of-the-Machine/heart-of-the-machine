package com.github.hotm.mod.world.gen.feature

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess

/**
 * Utilities for when generating features.
 */
object FeatureUtils {

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
