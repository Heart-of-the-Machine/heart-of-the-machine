package com.github.hotm.mod.world.gen.feature.tree

import com.github.hotm.mod.util.GeometryUtils
import com.github.hotm.mod.world.gen.feature.HotMFeatures
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.TestableWorld
import net.minecraft.world.gen.feature.TreeFeatureConfig
import net.minecraft.world.gen.foliage.FoliagePlacer
import net.minecraft.world.gen.trunk.TrunkPlacer
import net.minecraft.world.gen.trunk.TrunkPlacerType
import java.util.function.BiConsumer

class SolarArrayTrunkPlacer(baseHeight: Int, firstRandomHeight: Int, secondRandomHeight: Int) :
    TrunkPlacer(baseHeight, firstRandomHeight, secondRandomHeight) {
    companion object {
        val CODEC: Codec<SolarArrayTrunkPlacer> = RecordCodecBuilder.create { instance ->
            fillTrunkPlacerFields(instance).apply(instance, ::SolarArrayTrunkPlacer)
        }
    }

    override fun getType(): TrunkPlacerType<*> = HotMFeatures.SOLAR_ARRAY_TRUNK_PLACER

    override fun generate(
        world: TestableWorld, replacer: BiConsumer<BlockPos, BlockState>, random: RandomGenerator, height: Int,
        startPos: BlockPos, config: TreeFeatureConfig
    ): List<FoliagePlacer.TreeNode> {
        val foliage = mutableListOf<FoliagePlacer.TreeNode>()
        val mutable = BlockPos.Mutable()

        val downPos = startPos.down()
        setToDirt(world, replacer, random, downPos, config)
        setToDirt(world, replacer, random, downPos.north(), config)
        setToDirt(world, replacer, random, downPos.south(), config)
        setToDirt(world, replacer, random, downPos.west(), config)
        setToDirt(world, replacer, random, downPos.east(), config)

        // center bit
        for (i in 0..height) {
            mutable.set(startPos, 0, i, 0)
            placeTrunkBlock(world, replacer, random, mutable, config)
        }
        foliage += FoliagePlacer.TreeNode(startPos.up(height), 2, false)

        // side bits
        val sideHeight = height / 2 + random.nextInt(5) - 2
        val sideRadius = (height / 2 + random.nextInt(3) - 5).coerceAtLeast(3)
        val inner = Vec3d(sideRadius - 0.5, sideRadius - 0.5, sideRadius - 0.5)
        val outer = Vec3d(sideRadius + 0.5, sideRadius + 0.5, sideRadius + 0.5)
        for (dir in (0 until 4).map(Direction::fromHorizontal)) {
            // lower

            for (i in 0 until sideHeight) {
                mutable.set(startPos, dir)
                mutable.move(0, i, 0)
                placeTrunkBlock(world, replacer, random, mutable, config)
            }

            // upper
            val rPos = startPos.offset(dir).up(sideHeight)
            val center = Vec3d.ofCenter(rPos.up(sideRadius))
            for (r in 0..sideRadius) {
                for (y in 0..sideRadius) {
                    mutable.set(rPos)
                    mutable.move(dir, r)
                    mutable.move(0, y, 0)
                    val vecPos = Vec3d.ofCenter(mutable)

                    if (GeometryUtils.inEllipsoid(center, outer, vecPos)
                        && !GeometryUtils.inEllipsoid(center, inner, vecPos)
                    ) {
                        placeTrunkBlock(world, replacer, random, mutable, config)
                    }
                }
            }
            foliage += FoliagePlacer.TreeNode(rPos.offset(dir, sideRadius).up(sideRadius), 0, false)
        }

        return foliage
    }
}
