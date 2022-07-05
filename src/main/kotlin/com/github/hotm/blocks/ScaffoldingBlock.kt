package com.github.hotm.blocks

import com.github.hotm.misc.HotMBlockTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.Waterloggable
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import kotlin.math.min

class ScaffoldingBlock(settings: Settings) : Block(settings), Waterloggable {
    companion object {
        private val NORMAL_OUTLINE_SHAPE: VoxelShape
        private val BOTTOM_OUTLINE_SHAPE: VoxelShape
        private val COLLISION_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0)
        private val OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0)
        val DISTANCE = Properties.DISTANCE_0_7
        val WATERLOGGED = Properties.WATERLOGGED
        val BOTTOM = Properties.BOTTOM

        init {
            val voxelShape = createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0)
            val voxelShape2 = createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0)
            val voxelShape3 = createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0)
            val voxelShape4 = createCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0)
            val voxelShape5 = createCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0)
            NORMAL_OUTLINE_SHAPE = VoxelShapes.union(voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5)
            val voxelShape6 = createCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0)
            val voxelShape7 = createCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0)
            val voxelShape8 = createCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0)
            val voxelShape9 = createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0)
            BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(
                COLLISION_SHAPE,
                NORMAL_OUTLINE_SHAPE,
                voxelShape7,
                voxelShape6,
                voxelShape9,
                voxelShape8
            )
        }

        fun calculateDistance(world: BlockView, pos: BlockPos): Int {
            val mutable = pos.mutableCopy().move(Direction.DOWN)
            val blockState = world.getBlockState(mutable)
            var i = 7

            if (HotMBlockTags.SCAFFOLDING.contains(blockState.block)) {
                i = blockState.get(DISTANCE)
            } else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP)) {
                return 0
            }

            for (direction in Direction.Type.HORIZONTAL) {
                val blockState2 = world.getBlockState(mutable.set(pos, direction))
                if (HotMBlockTags.SCAFFOLDING.contains(blockState2.block)) {
                    i = min(i, blockState2.get(DISTANCE) + 1)
                    if (i == 1) {
                        break
                    }
                }
            }
            return i
        }
    }

    init {
        defaultState =
            stateManager.defaultState.with(DISTANCE, 7).with(WATERLOGGED, false)
                .with(BOTTOM, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(DISTANCE, WATERLOGGED, BOTTOM)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        return if (!context.isHolding(state.block.asItem())) {
            if (state.get(BOTTOM)) BOTTOM_OUTLINE_SHAPE else NORMAL_OUTLINE_SHAPE
        } else {
            VoxelShapes.fullCube()
        }
    }

    override fun getRaycastShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return VoxelShapes.fullCube()
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        return context.stack.item === asItem()
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val blockPos = ctx.blockPos
        val world = ctx.world
        val i = calculateDistance(world, blockPos)
        return defaultState.with(WATERLOGGED, world.getFluidState(blockPos).fluid === Fluids.WATER).with(DISTANCE, i)
            .with(BOTTOM, shouldBeBottom(world, blockPos, i))
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        if (!world.isClient) {
            world.createAndScheduleBlockTick(pos, this, 1)
        }
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        newState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        posFrom: BlockPos
    ): BlockState {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        if (!world.isClient) {
            world.createAndScheduleBlockTick(pos, this, 1)
        }
        return state
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val i = calculateDistance(world, pos)
        val blockState = (state.with(DISTANCE, i)).with(
            BOTTOM,
            shouldBeBottom(world, pos, i)
        )
        if (blockState.get(DISTANCE) == 7) {
            if (state.get(DISTANCE) == 7) {
                FallingBlockEntity.spawnFromBlock(world, pos, blockState)
            } else {
                world.breakBlock(pos, true)
            }
        } else if (state !== blockState) {
            world.setBlockState(pos, blockState, 3)
        }
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return calculateDistance(world, pos) < 7
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return if (context.isAbove(VoxelShapes.fullCube(), pos, true) && !context.isDescending) {
            NORMAL_OUTLINE_SHAPE
        } else {
            if (state.get(DISTANCE) != 0 && state.get(BOTTOM) && context.isAbove(
                    OUTLINE_SHAPE, pos, true
                )
            ) COLLISION_SHAPE else VoxelShapes.empty()
        }
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(
            state
        )
    }

    private fun shouldBeBottom(world: BlockView, pos: BlockPos, distance: Int): Boolean {
        return distance > 0 && !world.getBlockState(pos.down()).isOf(this)
    }
}
