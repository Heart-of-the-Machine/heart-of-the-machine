package com.github.hotm.mod.world.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext

class CrystalGrowthFeature(codec: Codec<CrystalGrowthConfig>) : Feature<CrystalGrowthConfig>(codec) {
    override fun place(ctx: FeatureContext<CrystalGrowthConfig>): Boolean {
        val world = ctx.world
        val pos = ctx.origin
        val config = ctx.config
        return if (world.isAir(pos) && nextTo(world, pos, config) && beneathSomething(world, pos)) {
            placeCrystals(world, pos, ctx.random, config.size, config)
            true
        } else {
            false
        }
    }

    private fun nextTo(world: ServerWorldAccess, pos: BlockPos, config: CrystalGrowthConfig): Boolean {
        for (dir in Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).isIn(config.targets)) {
                return true
            }
        }
        return false
    }

    private fun beneathSomething(world: ServerWorldAccess, pos: BlockPos): Boolean {
        val mutable = pos.mutableCopy()
        for (y in pos.y until world.topY) {
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
        random: RandomGenerator,
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
