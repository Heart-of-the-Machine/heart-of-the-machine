package com.github.hotm.world.gen.surfacebuilder

import com.mojang.serialization.Codec
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder
import java.util.*

class NectereSurfaceBuilder(codec: Codec<NectereSurfaceConfig>) : SurfaceBuilder<NectereSurfaceConfig>(codec) {
    override fun generate(
        random: Random,
        chunk: Chunk,
        biome: Biome,
        x: Int,
        z: Int,
        height: Int,
        noise: Double,
        defaultBlock: BlockState,
        fluidBlock: BlockState,
        seaLevel: Int,
        seed: Long,
        ternarySurfaceConfig: NectereSurfaceConfig
    ) {
        this.generate(
            random,
            chunk,
            biome,
            x,
            z,
            height,
            noise,
            defaultBlock,
            fluidBlock,
            ternarySurfaceConfig.topMaterial,
            ternarySurfaceConfig.underMaterial,
            ternarySurfaceConfig.beachMaterial,
            seaLevel
        )
    }

    private fun generate(
        random: Random,
        chunk: Chunk,
        biome: Biome,
        x: Int,
        z: Int,
        height: Int,
        noise: Double,
        defaultBlock: BlockState,
        fluidBlock: BlockState,
        topBlock: BlockState,
        underBlock: BlockState,
        beachBlock: BlockState,
        seaLevel: Int
    ) {
        var surfaceState = topBlock
        var underState = underBlock
        val mutable = BlockPos.Mutable()
        var curDepth = -1
        val surfaceDepth = (noise / 3.0 + 3.0 + random.nextDouble() * 0.25).toInt()
        val chunkX = x and 15
        val chunkZ = z and 15

        for (y in height downTo 0) {
            mutable[chunkX, y] = chunkZ
            val curState = chunk.getBlockState(mutable)

            if (curState.isAir) {
                curDepth = -1
            } else if (curState.isOf(defaultBlock.block)) {
                if (curDepth == -1) {
                    if (surfaceDepth <= 0) {
                        surfaceState = Blocks.AIR.defaultState
                        underState = defaultBlock
                    } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                        surfaceState = topBlock
                        underState = underBlock
                    }

                    if (y < seaLevel && surfaceState.isAir) {
                        surfaceState = if (biome.getTemperature(mutable.set(x, y, z)) < 0.15f) {
                            Blocks.ICE.defaultState
                        } else {
                            fluidBlock
                        }
                        mutable[chunkX, y] = chunkZ
                    }

                    curDepth = surfaceDepth

                    if (y >= seaLevel) {
                        chunk.setBlockState(mutable, surfaceState, false)
                    } else {
                        surfaceState = Blocks.AIR.defaultState
                        underState = beachBlock
                        chunk.setBlockState(mutable, beachBlock, false)
                    }
                } else if (curDepth > 0) {
                    --curDepth
                    chunk.setBlockState(mutable, underState, false)
                }
            }
        }
    }
}