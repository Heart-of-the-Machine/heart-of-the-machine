package com.github.hotm.mod.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView


open class PlasseinPlantBlock(settings: Settings) : Block(settings) {
    protected open fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return floor.isIn(HotMBlockTags.PLASSEIN_FERTILE)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (!state.canPlaceAt(world, pos)) Blocks.AIR.defaultState else super.getStateForNeighborUpdate(
            state,
            direction,
            neighborState,
            world,
            pos,
            neighborPos
        )
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockPos = pos.down()
        return canPlantOnTop(world.getBlockState(blockPos), world, blockPos)
    }

    override fun isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return state.fluidState.isEmpty
    }

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType): Boolean {
        return if (type == NavigationType.AIR && !collidable) true else super.canPathfindThrough(
            state,
            world,
            pos,
            type
        )
    }
}
