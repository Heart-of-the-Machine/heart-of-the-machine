package com.github.hotm.blocks

import com.github.hotm.HotMBlockTags
import com.github.hotm.HotMProperties
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import java.util.*

class PlasseinLeavesBlock(settings: Settings) : Block(settings) {
    companion object {
        const val MAX_DISTANCE = 16
        val DISTANCE = HotMProperties.DISTANCE
        val PERSISTENT = Properties.PERSISTENT
    }

    init {
        defaultState = stateManager.defaultState.with(DISTANCE, MAX_DISTANCE).with(PERSISTENT, false)
    }

    override fun getSidesShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return state.get(DISTANCE) == MAX_DISTANCE && !state.get(PERSISTENT)
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (!state.get(PERSISTENT) && state.get(DISTANCE) == MAX_DISTANCE) {
            dropStacks(state, world, pos)
            world.removeBlock(pos, false)
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), 3)
    }

    override fun getOpacity(state: BlockState, world: BlockView, pos: BlockPos): Int {
        return 1
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        newState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        posFrom: BlockPos
    ): BlockState {
        val i = getDistanceFromLog(newState) + 1
        if (i != 1 || state.get(DISTANCE) != i) {
            world.blockTickScheduler.schedule(pos, this, 1)
        }
        return state
    }

    private fun updateDistanceFromLogs(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        var i = MAX_DISTANCE
        val mutable = BlockPos.Mutable()
        for (direction in Direction.values()) {
            mutable[pos] = direction
            i = i.coerceAtMost(getDistanceFromLog(world.getBlockState(mutable)) + 1)
            if (i == 1) {
                break
            }
        }
        return state.with(DISTANCE, i)
    }

    private fun getDistanceFromLog(state: BlockState): Int {
        return if (HotMBlockTags.PLASSEIN_SOURCE.contains(state.block)) {
            0
        } else {
            if (state.block is PlasseinLeavesBlock) state.get(DISTANCE) else MAX_DISTANCE
        }
    }

    @Environment(EnvType.CLIENT)
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (world.hasRain(pos.up())) {
            if (random.nextInt(15) == 1) {
                val blockPos = pos.down()
                val blockState = world.getBlockState(blockPos)
                if (!blockState.isOpaque || !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
                    val d = pos.x.toDouble() + random.nextDouble()
                    val e = pos.y.toDouble() - 0.05
                    val f = pos.z.toDouble() + random.nextDouble()
                    world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0)
                }
            }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(DISTANCE, PERSISTENT)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return updateDistanceFromLogs(
            defaultState.with(PERSISTENT, true),
            ctx.world,
            ctx.blockPos
        )
    }
}