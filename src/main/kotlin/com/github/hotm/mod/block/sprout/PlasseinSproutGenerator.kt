package com.github.hotm.mod.block.sprout

import com.github.hotm.mod.block.HotMBlockTags
import kotlin.jvm.optionals.getOrNull
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.BlockView
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.ConfiguredFeature

abstract class PlasseinSproutGenerator {
    protected abstract fun createGrowthFeature(random: RandomGenerator, leyline: Boolean): RegistryKey<ConfiguredFeature<*, *>>?
    protected abstract fun createLargeGrowthFeature(random: RandomGenerator): RegistryKey<ConfiguredFeature<*, *>>?
    protected abstract fun createCrossGrowthFeature(random: RandomGenerator, leyline: Boolean): RegistryKey<ConfiguredFeature<*, *>>?

    open fun generate(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator
    ): Boolean {
        return when {
            findCrossGrowthPos(world, generator, growthPos, growthState, random) -> true
            findLargeGrowthPos(world, generator, growthPos, growthState, random) -> true
            generateSmallGrowth(world, generator, growthPos, growthState, random) -> true
            else -> false
        }
    }

    open fun generateSmallGrowth(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator
    ): Boolean {
        val key = createGrowthFeature(random, shouldGenerateLeyline(world, growthPos, 0, 0)) ?: return false
        val holder = world.registryManager[RegistryKeys.CONFIGURED_FEATURE].getHolder(key).getOrNull() ?: return false
        val feature = holder.value()

        world.setBlockState(growthPos, Blocks.AIR.defaultState, 4)
        return if (feature.generate(world, generator, random, growthPos)) {
            true
        } else {
            world.setBlockState(growthPos, growthState, 4)
            false
        }
    }

    open fun findLargeGrowthPos(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator
    ): Boolean {
        for (x in 0 downTo -1) {
            for (z in 0 downTo -1) {
                if (canGenerateLargeGrowth(growthState, world, growthPos, x, z)) {
                    return generateLargeGrowth(world, generator, growthPos, growthState, random, x, z)
                }
            }
        }

        return false
    }

    open fun canGenerateLargeGrowth(state: BlockState, world: BlockView, pos: BlockPos, offsetX: Int, offsetZ: Int): Boolean {
        val block = state.block
        return block === world.getBlockState(pos.add(offsetX, 0, offsetZ)).block
                && block === world.getBlockState(pos.add(offsetX + 1, 0, offsetZ)).block
                && block === world.getBlockState(pos.add(offsetX, 0, offsetZ + 1)).block
                && block === world.getBlockState(pos.add(offsetX + 1, 0, offsetZ + 1)).block
    }

    open fun generateLargeGrowth(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator,
        offsetX: Int,
        offsetZ: Int
    ): Boolean {
        val key = createLargeGrowthFeature(random) ?: return false
        val holder = world.registryManager[RegistryKeys.CONFIGURED_FEATURE].getHolder(key).getOrNull() ?: return false
        val feature = holder.value()

        val air = Blocks.AIR.defaultState
        world.setBlockState(growthPos.add(offsetX, 0, offsetZ), air, 4)
        world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ), air, 4)
        world.setBlockState(growthPos.add(offsetX, 0, offsetZ + 1), air, 4)
        world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ + 1), air, 4)

        return if (feature.generate(world, generator, random, growthPos.add(offsetX, 0, offsetZ))) {
            true
        } else {
            world.setBlockState(growthPos.add(offsetX, 0, offsetZ), growthState, 4)
            world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ), growthState, 4)
            world.setBlockState(growthPos.add(offsetX, 0, offsetZ + 1), growthState, 4)
            world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ + 1), growthState, 4)
            false
        }
    }

    open fun findCrossGrowthPos(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator
    ): Boolean {
        if (canGenerateCrossGrowth(growthState, world, growthPos, 0, 0)) {
            return generateCrossGrowth(world, generator, growthPos, growthState, random, 0, 0)
        }

        for (i in 0 until 4) {
            val dir = Direction.fromHorizontal(i)
            if (canGenerateCrossGrowth(growthState, world, growthPos, dir.offsetX, dir.offsetZ)) {
                return generateCrossGrowth(world, generator, growthPos, growthState, random, dir.offsetX, dir.offsetZ)
            }
        }

        return false
    }

    open fun canGenerateCrossGrowth(state: BlockState, world: BlockView, pos: BlockPos, offsetX: Int, offsetZ: Int): Boolean {
        val block = state.block
        return block === world.getBlockState(pos.add(offsetX, 0, offsetZ)).block
                && block === world.getBlockState(pos.add(offsetX - 1, 0, offsetZ)).block
                && block === world.getBlockState(pos.add(offsetX + 1, 0, offsetZ)).block
                && block === world.getBlockState(pos.add(offsetX, 0, offsetZ - 1)).block
                && block === world.getBlockState(pos.add(offsetX, 0, offsetZ + 1)).block
    }

    open fun generateCrossGrowth(
        world: ServerWorld,
        generator: ChunkGenerator,
        growthPos: BlockPos,
        growthState: BlockState,
        random: RandomGenerator,
        offsetX: Int,
        offsetZ: Int
    ): Boolean {
        val key = createCrossGrowthFeature(random, shouldGenerateLeyline(world, growthPos, offsetX, offsetZ)) ?: return false
        val holder = world.registryManager[RegistryKeys.CONFIGURED_FEATURE].getHolder(key).getOrNull() ?: return false
        val feature = holder.value()

        val air = Blocks.AIR.defaultState
        world.setBlockState(growthPos.add(offsetX, 0, offsetZ), air, 4)
        world.setBlockState(growthPos.add(offsetX - 1, 0, offsetZ), air, 4)
        world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ), air, 4)
        world.setBlockState(growthPos.add(offsetX, 0, offsetZ - 1), air, 4)
        world.setBlockState(growthPos.add(offsetX, 0, offsetZ + 1), air, 4)

        return if (feature.generate(world, generator, random, growthPos.add(offsetX, 0, offsetZ))) {
            true
        } else {
            world.setBlockState(growthPos.add(offsetX, 0, offsetZ), growthState, 4)
            world.setBlockState(growthPos.add(offsetX - 1, 0, offsetZ), growthState, 4)
            world.setBlockState(growthPos.add(offsetX + 1, 0, offsetZ), growthState, 4)
            world.setBlockState(growthPos.add(offsetX, 0, offsetZ - 1), growthState, 4)
            world.setBlockState(growthPos.add(offsetX, 0, offsetZ + 1), growthState, 4)
            false
        }
    }

    open fun shouldGenerateLeyline(world: ServerWorld, growthPos: BlockPos, offsetX: Int, offsetZ: Int): Boolean {
        return world.getBlockState(growthPos.add(offsetX, -1, offsetZ)).isIn(HotMBlockTags.LEYLINES)
    }
}
