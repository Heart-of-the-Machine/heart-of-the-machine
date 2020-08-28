package com.github.hotm.blocks

import com.github.hotm.gen.HotMDimensions
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class NecterePortalBlock(settings: Settings) : FacingBlock(settings) {
    companion object {
        val Y_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0)
        val Z_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0)
        val X_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0)
    }

    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.UP)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.with(FACING, mirror.apply(state.get(FACING)))
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        return when (state.get(FACING).axis) {
            Direction.Axis.X -> X_SHAPE
            Direction.Axis.Z -> Z_SHAPE
            Direction.Axis.Y -> Y_SHAPE
            else -> X_SHAPE
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val direction = ctx.side
        val blockState = ctx.world.getBlockState(ctx.blockPos.offset(direction.opposite))
        return if (blockState.isOf(this) && blockState.get(FACING) == direction) defaultState.with(
            FACING,
            direction.opposite
        ) else defaultState.with(FACING, direction)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPistonBehavior(state: BlockState): PistonBehavior? {
        return PistonBehavior.BLOCK
    }

    override fun canPathfindThrough(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        type: NavigationType
    ): Boolean {
        return false
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (state.get(FACING) == Direction.UP && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            HotMDimensions.attemptNectereTeleportation(entity, world, pos)
        }
    }
}