package com.github.hotm.mod.block

import com.github.hotm.mod.block.sprout.PlasseinSproutGenerator
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Fertilizable
import net.minecraft.block.ShapeContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

open class PlasseinSproutBlock(private val generator: PlasseinSproutGenerator, settings: Settings) :
    PlasseinPlantBlock(settings), Fertilizable {
    init {
        defaultState =
            stateManager.defaultState.with(STAGE, Integer.valueOf(0))
    }

    override fun getOutlineShape(
        state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
            generate(world, pos, state, random)
        }
    }

    fun generate(world: ServerWorld, pos: BlockPos, state: BlockState, random: RandomGenerator) {
        if (state.get(STAGE) == 0) {
            world.setBlockState(pos, state.cycle(STAGE), NO_REDRAW)
        } else {
            generator.generate(world, world.chunkManager.chunkGenerator, pos, state, random)
        }
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean): Boolean {
        return true
    }

    override fun canFertilize(world: World, random: RandomGenerator, pos: BlockPos, state: BlockState): Boolean {
        return world.random.nextFloat().toDouble() < 0.45
    }

    override fun fertilize(world: ServerWorld, random: RandomGenerator, pos: BlockPos, state: BlockState) {
        generate(world, pos, state, random)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(STAGE)
    }

    companion object {
        val STAGE: IntProperty = Properties.STAGE
        protected val SHAPE: VoxelShape = createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)
    }
}
