package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

class RefusePileFeature(codec: Codec<PileFeatureConfig?>?) : Feature<PileFeatureConfig>(codec) {
    override fun generate(
        serverWorldAccess: StructureWorldAccess,
        chunkGenerator: ChunkGenerator,
        random: Random,
        blockPos: BlockPos,
        boulderFeatureConfig: PileFeatureConfig
    ): Boolean {
        var blockPosMut = blockPos

        while (true) {
            if (blockPosMut.y > 3) {
                if (!FeatureUtils.isSurface(serverWorldAccess.getBlockState(blockPosMut.down()).block)) {
                    blockPosMut = blockPosMut.down()
                    continue
                }
            }

            if (blockPosMut.y <= 3) {
                return false
            }

            val startRadius = boulderFeatureConfig.startRadius
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
                            serverWorldAccess.setBlockState(blockPos2, boulderFeatureConfig.state, 4)
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