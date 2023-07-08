package com.github.hotm.mod.block

import java.util.OptionalInt
import com.github.hotm.mod.misc.HotMProperties
import kotlin.math.min
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Waterloggable
import net.minecraft.client.util.ParticleUtil
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class PlasseinLeavesBlock(settings: Settings) : Block(settings), Waterloggable {
    init {
        defaultState =
            stateManager.defaultState.with(DISTANCE, MAX_DISTANCE).with(PERSISTENT, false).with(WATERLOGGED, false)
    }

    override fun getSidesShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return state.get(DISTANCE) == MAX_DISTANCE && !state.get(PERSISTENT)
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
        if (canDecay(state)) {
            dropStacks(state, world, pos)
            world.removeBlock(pos, false)
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
        world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), NOTIFY_ALL)
    }

    override fun getOpacity(state: BlockState, world: BlockView, pos: BlockPos): Int {
        return 1
    }

    override fun getStateForNeighborUpdate(
        state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        val i = getDistanceFromLog(neighborState) + 1
        if (i != 1 || state.get(DISTANCE) != i) {
            world.scheduleBlockTick(pos, this, TICK_DELAY)
        }
        return state
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: RandomGenerator) {
        if (world.hasRain(pos.up())) {
            if (random.nextInt(15) == 1) {
                val blockPos = pos.down()
                val blockState = world.getBlockState(blockPos)
                if (!blockState.isOpaque || !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
                    ParticleUtil.spawnParticle(world, pos, random, ParticleTypes.DRIPPING_WATER)
                }
            }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(DISTANCE, PERSISTENT, WATERLOGGED)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val fluidState = ctx.world.getFluidState(ctx.blockPos)
        val blockState = defaultState
            .with(PERSISTENT, true)
            .with(WATERLOGGED, fluidState.fluid === Fluids.WATER)
        return updateDistanceFromLogs(blockState, ctx.world, ctx.blockPos)
    }

    companion object {
        const val MAX_DISTANCE = HotMProperties.MAX_DISTANCE
        val DISTANCE = HotMProperties.DISTANCE
        val PERSISTENT: BooleanProperty = Properties.PERSISTENT
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private const val TICK_DELAY = 1

        private fun updateDistanceFromLogs(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
            var i = MAX_DISTANCE
            val mutable = BlockPos.Mutable()
            for (direction in Direction.values()) {
                mutable[pos] = direction
                i = min(i, getDistanceFromLog(world.getBlockState(mutable)) + 1)
                if (i == 1) {
                    break
                }
            }
            return state.with(DISTANCE, i)
        }

        private fun getDistanceFromLog(state: BlockState): Int {
            return getOptionalDistanceFromLog(state).orElse(MAX_DISTANCE)
        }

        private fun getOptionalDistanceFromLog(state: BlockState): OptionalInt {
            return if (state.isIn(HotMBlockTags.PLASSEIN_SOURCE)) {
                OptionalInt.of(0)
            } else {
                if (state.contains(DISTANCE)) OptionalInt.of(state.get(DISTANCE)) else OptionalInt.empty()
            }
        }

        private fun canDecay(state: BlockState): Boolean {
            return !state.get(PERSISTENT) && state.get(DISTANCE) == MAX_DISTANCE
        }
    }
}
