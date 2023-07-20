package com.github.hotm.mod.block

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class PlasseinFlowerBlock(settings: Settings) : PlasseinPlantBlock(settings) {
    companion object {
        private val SHAPE = createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    }

    override fun getOutlineShape(
        state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext
    ): VoxelShape {
        val offset = state.getModelOffset(world, pos)
        return SHAPE.offset(offset.x, offset.y, offset.z)
    }
}
