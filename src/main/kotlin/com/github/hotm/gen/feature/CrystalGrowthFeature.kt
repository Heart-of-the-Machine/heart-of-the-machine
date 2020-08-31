package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

class CrystalGrowthFeature(codec: Codec<CrystalGrowthConfig>) : Feature<CrystalGrowthConfig>(codec) {
    override fun generate(
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: CrystalGrowthConfig
    ): Boolean {
        return if (world.isAir(pos) && nextTo(world, pos, config) && beneathSomething(world, pos)) {
            placeCrystals(world, pos, random, config.size, config)
            true
        } else {
            false
        }
    }

    private fun nextTo(world: ServerWorldAccess, pos: BlockPos, config: CrystalGrowthConfig): Boolean {
        for (dir in Direction.values()) {
            if (config.targets.contains(world.getBlockState(pos.offset(dir)).block)) {
                return true
            }
        }
        return false
    }

    private fun beneathSomething(world: ServerWorldAccess, pos: BlockPos): Boolean {
        val mutable = pos.mutableCopy()
        for (y in pos.y until 256) {
            mutable.y = y
            if (!world.isAir(mutable)) {
                return true
            }
        }
        return false
    }

    private fun placeCrystals(
        world: ServerWorldAccess,
        pos: BlockPos,
        random: Random,
        remaining: Int,
        config: CrystalGrowthConfig
    ) {
        if (world.isAir(pos) && remaining > 0) {
            world.setBlockState(pos, config.crystal, 3)

            placeCrystals(world, pos.offset(Direction.random(random)), random, remaining - 1, config)

            while (random.nextFloat() < config.splitChance) {
                placeCrystals(world, pos.offset(Direction.random(random)), random, remaining - 1, config)
            }
        }
    }
}