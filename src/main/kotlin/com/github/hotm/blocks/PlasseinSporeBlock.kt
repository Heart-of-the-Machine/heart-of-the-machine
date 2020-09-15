package com.github.hotm.blocks

import com.github.hotm.blocks.spore.PlasseinSporeGenerator
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Fertilizable
import net.minecraft.block.ShapeContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class PlasseinSporeBlock(private val generator: PlasseinSporeGenerator, settings: Settings) :
    PlasseinPlantBlock(settings), Fertilizable {
    companion object {
        val STAGE = Properties.STAGE
        val SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)
    }

    init {
        defaultState = stateManager.defaultState.with(STAGE, 0)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
            generate(world, pos, state, random)
        }
    }

    fun generate(serverWorld: ServerWorld, blockPos: BlockPos, blockState: BlockState, random: Random) {
        if (blockState.get(STAGE) == 0) {
            serverWorld.setBlockState(blockPos, blockState.cycle(STAGE), 4)
        } else {
            generator.generate(
                serverWorld, serverWorld.chunkManager.chunkGenerator,
                blockPos, blockState, random
            )
        }
    }

    override fun isFertilizable(world: BlockView, pos: BlockPos, state: BlockState, isClient: Boolean): Boolean {
        return true
    }

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState): Boolean {
        return world.random.nextFloat() < 0.45
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        generate(world, pos, state, random)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(STAGE)
    }
}