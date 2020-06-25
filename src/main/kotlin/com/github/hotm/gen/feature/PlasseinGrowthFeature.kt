package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.TestableWorld
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*
import kotlin.collections.ArrayList

class PlasseinGrowthFeature(codec: Codec<PlasseinGrowthConfig>) : Feature<PlasseinGrowthConfig>(codec) {
    override fun generate(
        serverWorldAccess: ServerWorldAccess,
        accessor: StructureAccessor,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: PlasseinGrowthConfig
    ): Boolean {
        val stalkBlocks = ArrayList<BlockPos>()
        val leafBlocks = ArrayList<BlockPos>()

        if (!tryGenerate(serverWorldAccess, random, pos, config, stalkBlocks, leafBlocks)) {
            return false
        }

        for (stalk in stalkBlocks) {
            serverWorldAccess.setBlockState(stalk, config.stalk, 0x13)
        }
        for (leaf in leafBlocks) {
            serverWorldAccess.setBlockState(leaf, config.leaves, 0x13)
        }

        return true
    }

    private fun tryGenerate(
        world: TestableWorld,
        random: Random,
        pos: BlockPos,
        config: PlasseinGrowthConfig,
        stalkBlocks: MutableCollection<BlockPos>,
        leafBlocks: MutableCollection<BlockPos>
    ): Boolean {
        if (!world.testBlockState(pos.down()) { FeatureUtils.isSurface(it.block) }) {
            return false
        }

        val mutable = pos.mutableCopy()
        val height = random.nextInt(config.heightVariation) + config.heightMin
        var sway = config.sway
        val swayDirection = Direction.byId(random.nextInt(4) + 2)

        // TODO change nearby ground to plassein variant for roots

        for (i in 0 until height) {
            if (!tryPlace(world, mutable, stalkBlocks)) {
                return false
            }

            if (i == height - 1) {
                if (!(tryPlace(world, mutable.north(), leafBlocks)
                            && tryPlace(world, mutable.east(), leafBlocks)
                            && tryPlace(world, mutable.south(), leafBlocks)
                            && tryPlace(world, mutable.west(), leafBlocks))
                ) {
                    return false
                }
            } else {
                if (random.nextDouble() < 0.5 && !placeLeafBy(world, random, mutable, leafBlocks)) {
                    return false
                }
                if (random.nextDouble() < 0.25 && !placeLeafBy(world, random, mutable, leafBlocks)) {
                    return false
                }
            }

            if (i < height - 1 && random.nextDouble() < sway) {
                sway *= config.swayMultiplier
                mutable.move(swayDirection)
            }
            mutable.move(Direction.UP)
        }

        return tryPlace(world, mutable, leafBlocks)
    }

    private fun placeLeafBy(
        world: TestableWorld,
        random: Random,
        pos: BlockPos,
        leafBlocks: MutableCollection<BlockPos>
    ): Boolean {
        return tryPlace(world, pos.offset(Direction.byId(random.nextInt(4) + 2)), leafBlocks)
    }

    private fun tryPlace(world: TestableWorld, pos: BlockPos, blocks: MutableCollection<BlockPos>): Boolean {
        return if (world.testBlockState(pos) { it.isAir }) {
            blocks += pos.toImmutable()
            true
        } else {
            false
        }
    }
}