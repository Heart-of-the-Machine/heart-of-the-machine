package com.github.hotm.gen.feature.segment

import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView

object FeatureSegmentUtils {
    fun tryFill(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        world: WorldView,
        min: BlockPos,
        max: BlockPos,
        placement: BlockPlacement
    ): Boolean {
        val mutable = BlockPos.Mutable()
        for (y in min.y..max.y) {
            for (x in min.x..max.x) {
                for (z in min.z..max.z) {
                    mutable.set(x, y, z)

                    if (!world.isAir(mutable)) {
                        return false
                    }

                    place(blocks, mutable, placement)
                }
            }
        }

        return true
    }

    fun tryPlace(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        world: WorldView,
        pos: BlockPos,
        placement: BlockPlacement
    ): Boolean {
        if (world.isAir(pos)) {
            place(blocks, pos, placement)
            return true
        }

        return false
    }

    fun place(blocks: MutableMap<BlockPos, BlockPlacement>, pos: BlockPos, placement: BlockPlacement) {
        val immutable = pos.toImmutable()
        if (blocks.containsKey(immutable)) {
            val previous = blocks[immutable]!!
            // lower priority value means higher priority
            if (previous.priority > placement.priority) {
                blocks[immutable] = BlockPlacement(
                    placement.state,
                    previous.replaceTerrain || placement.replaceTerrain,
                    placement.flags,
                    placement.priority
                )
            }
        } else {
            blocks[immutable] = placement
        }
    }
}