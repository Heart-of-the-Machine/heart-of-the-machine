package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

class PlasseinGrowthFeature(codec: Codec<PlasseinGrowthConfig>) : Feature<PlasseinGrowthConfig>(codec) {
    override fun generate(
        serverWorldAccess: ServerWorldAccess,
        accessor: StructureAccessor,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: PlasseinGrowthConfig
    ): Boolean {
        val mutable = pos.mutableCopy()
        val height = random.nextInt(config.heightVariation) + config.heightMin
        var sway = config.sway
        val swayDirection = Direction.byId(random.nextInt(4) + 2)

        // TODO change nearby ground to plassein variant for roots

        for (i in 0 until height) {
            serverWorldAccess.setBlockState(mutable, config.stalk, 0x13)

            if (i == height - 1) {
                serverWorldAccess.setBlockState(mutable.north(), config.leaves, 0x13)
                serverWorldAccess.setBlockState(mutable.east(), config.leaves, 0x13)
                serverWorldAccess.setBlockState(mutable.south(), config.leaves, 0x13)
                serverWorldAccess.setBlockState(mutable.west(), config.leaves, 0x13)
            } else {
                if (random.nextDouble() < 0.5) {
                    placeLeafBy(serverWorldAccess, random, mutable, config.leaves)
                }
                if (random.nextDouble() < 0.25) {
                    placeLeafBy(serverWorldAccess, random, mutable, config.leaves)
                }
            }

            if (i < height - 1 && random.nextDouble() < sway) {
                sway *= config.swayMultiplier
                mutable.move(swayDirection)
            }
            mutable.move(Direction.UP)
        }

        serverWorldAccess.setBlockState(mutable, config.leaves, 0x13)

        return true
    }

    private fun placeLeafBy(world: ServerWorldAccess, random: Random, pos: BlockPos, leaf: BlockState) {
        world.setBlockState(pos.offset(Direction.byId(random.nextInt(4) + 2)), leaf, 0x13)
    }
}