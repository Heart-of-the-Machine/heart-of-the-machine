package com.github.hotm.world.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext
import java.util.*

class RefusePileFeature(codec: Codec<PileFeatureConfig?>?) : Feature<PileFeatureConfig>(codec) {
    override fun generate(ctx: FeatureContext<PileFeatureConfig>): Boolean {
        val world = ctx.world
        val config = ctx.config
        val random = ctx.random
        var blockPosMut = ctx.origin

        while (true) {
            if (blockPosMut.y > 3) {
                if (!FeatureUtils.isSurface(world.getBlockState(blockPosMut.down()))) {
                    blockPosMut = blockPosMut.down()
                    continue
                }
            }

            if (blockPosMut.y <= 3) {
                return false
            }

            val startRadius = config.startRadius
            if (startRadius >= 0) {
                for (i in 0 until 3) {
                    val sizeX = startRadius + random.nextInt(2)
                    val sizeY = startRadius + random.nextInt(2)
                    val sizeZ = startRadius + random.nextInt(2)
                    val f = (sizeX + sizeY + sizeZ).toFloat() * 0.333f + 0.5f

                    for (blockPos2 in BlockPos.iterate(
                        blockPosMut.add(-sizeX, -sizeY, -sizeZ),
                        blockPosMut.add(sizeX, sizeY, sizeZ)
                    )) {
                        if (blockPos2.getSquaredDistance(blockPosMut) <= (f * f).toDouble()) {
                            world.setBlockState(blockPos2, config.state, 4)
                        }
                    }

                    blockPosMut = blockPosMut.add(
                        -(startRadius + 1) + random.nextInt(2 + startRadius * 2),
                        -random.nextInt(2),
                        -(startRadius + 1) + random.nextInt(2 + startRadius * 2)
                    )
                }
            }

            return true
        }
    }
}