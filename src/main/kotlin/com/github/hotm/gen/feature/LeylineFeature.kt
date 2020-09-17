package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.blocks.Leylineable
import com.github.hotm.util.DirectionUtils
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream

class LeylineFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    override fun generate(
        world: StructureWorldAccess,
        chunkGenerator: ChunkGenerator,
        _random: Random,
        blockPos: BlockPos,
        featureConfig: DefaultFeatureConfig
    ): Boolean {
        val chunkPos = ChunkPos(blockPos)
        val sources = getSources(world.seed, chunkGenerator, chunkPos)

        var generated = false
        for (source in sources) {
            if (canLeyline(world, source)) {
                generated = true
                makeLeyline(world, source)

                for (dir in DirectionUtils.horizontals()) {
                    val dests = Stream.concat(
                        getBoundaries(world.seed, chunkGenerator, chunkPos, dir),
                        getNeighborBoundaries(world.seed, chunkGenerator, chunkPos, dir)
                    )

                    for (dest in dests) {
                        makeLeylineTo(world, source, chunkPos, dest)
                    }
                }
            }
        }

        return generated
    }

    companion object {
        const val MAX_LEYLINE_HEIGHT = 160
        const val SOURCE_INDEX = 538872585
        const val SOURCE_STEP = 1005715095
        const val BORDER_INDEX = 1456570221
        const val BORDER_STEP = -1614785170
        const val DIRECTION_INDEX = 94861301
        const val MAX_LEYLINES_PER_CHUNK = 2
        const val MAX_BOUNDARIES_PER_CHUNNK_SIDE = 2
        const val MAX_LEYLINE_LENGTH = 24
        const val MAX_LEYLINE_LENGTH_SQR = MAX_LEYLINE_LENGTH * MAX_LEYLINE_LENGTH

        fun getChunkRandom(worldSeed: Long, pos: ChunkPos, index: Int, step: Int): ChunkRandom {
            val random = ChunkRandom()
            val populationSeed = random.setPopulationSeed(worldSeed, pos.startX, pos.startZ)
            random.setDecoratorSeed(populationSeed, index, step)
            return random
        }

        fun getSources(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            chunkPos: ChunkPos
        ): Stream<BlockPos> {
            val random = getChunkRandom(worldSeed, chunkPos, SOURCE_INDEX, SOURCE_STEP)

            val seaLevel = chunkGenerator.seaLevel
            return IntStream.range(0, MAX_LEYLINES_PER_CHUNK).mapToObj {
                BlockPos(
                    random.nextInt(16) + chunkPos.startX,
                    random.nextInt(MAX_LEYLINE_HEIGHT - seaLevel) + seaLevel,
                    random.nextInt(16) + chunkPos.startZ
                )
            }.filter {
                val sample = chunkGenerator.getColumnSample(it.x, it.z)
                sample.getBlockState(it).block is Leylineable
            }
        }

        fun getBoundaries(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            chunkPos: ChunkPos,
            direction: Direction
        ): Stream<BlockPos> {
            val seaLevel = chunkGenerator.seaLevel
            val random =
                getChunkRandom(worldSeed, chunkPos, BORDER_INDEX + DIRECTION_INDEX * direction.ordinal, BORDER_STEP)

            return IntStream.range(0, MAX_BOUNDARIES_PER_CHUNNK_SIDE).mapToObj {
                when (direction) {
                    Direction.NORTH -> BlockPos(
                        random.nextInt(16) + chunkPos.startX,
                        random.nextInt(MAX_LEYLINE_HEIGHT - seaLevel) + seaLevel,
                        chunkPos.startZ
                    )
                    Direction.SOUTH -> BlockPos(
                        random.nextInt(16) + chunkPos.startX,
                        random.nextInt(MAX_LEYLINE_HEIGHT - seaLevel) + seaLevel,
                        chunkPos.endZ
                    )
                    Direction.WEST -> BlockPos(
                        chunkPos.startX,
                        random.nextInt(MAX_LEYLINE_HEIGHT - seaLevel) + seaLevel,
                        random.nextInt(16) + chunkPos.startZ
                    )
                    Direction.EAST -> BlockPos(
                        chunkPos.endX,
                        random.nextInt(MAX_LEYLINE_HEIGHT - seaLevel) + seaLevel,
                        random.nextInt(16) + chunkPos.startZ
                    )
                    else -> throw IllegalStateException("Invalid horizontal: $direction")
                }
            }.filter {
                val sample = chunkGenerator.getColumnSample(it.x, it.z)
                sample.getBlockState(it).block is Leylineable
            }
        }

        fun makeLeyline(world: StructureWorldAccess, blockPos: BlockPos) {
            val state = world.getBlockState(blockPos)
            (state.block as? Leylineable)?.let { world.setBlockState(blockPos, it.getLeyline(state), 3) }
        }

        fun canLeyline(world: StructureWorldAccess, blockPos: BlockPos): Boolean {
            val block = world.getBlockState(blockPos).block
            return block is Leylineable || HotMBlocks.isLeyline(block)
        }

        fun getNextChunk(chunkPos: ChunkPos, direction: Direction): ChunkPos {
            return when (direction) {
                Direction.NORTH -> ChunkPos(chunkPos.x, chunkPos.z - 1)
                Direction.SOUTH -> ChunkPos(chunkPos.x, chunkPos.z + 1)
                Direction.WEST -> ChunkPos(chunkPos.x - 1, chunkPos.z)
                Direction.EAST -> ChunkPos(chunkPos.x + 1, chunkPos.z)
                else -> throw IllegalStateException("Invalid horizontal: $direction")
            }
        }

        fun getNeighborBoundaries(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            chunkPos: ChunkPos,
            direction: Direction
        ): Stream<BlockPos> {
            val nextChunk = getNextChunk(chunkPos, direction)
            val opposite = direction.opposite
            val nextBoundaries = getBoundaries(worldSeed, chunkGenerator, nextChunk, opposite)
            return nextBoundaries.map { it.offset(opposite) }
        }

        fun distanceSqrBetween(a: BlockPos, b: BlockPos): Int {
            val xDif = a.x - b.x
            val yDif = a.y - b.y
            val zDif = a.z - b.z
            return xDif * xDif + yDif * yDif + zDif * zDif
        }

        fun chunkContains(chunkPos: ChunkPos, blockPos: BlockPos): Boolean {
            return blockPos.x <= chunkPos.endX && blockPos.x >= chunkPos.startX
                    && blockPos.z <= chunkPos.endZ && blockPos.z >= chunkPos.startZ
        }

        fun makeLeylineTo(world: StructureWorldAccess, start: BlockPos, chunkPos: ChunkPos, end: BlockPos) {
            if (distanceSqrBetween(start, end) > MAX_LEYLINE_LENGTH_SQR) {
                return
            }

            val mutable = start.mutableCopy()
            val mutable2 = BlockPos.Mutable()

            while (true) {
                val curDistanceSqr = distanceSqrBetween(mutable, end)

                var nextDir: Direction? = null
                var nextDistance = curDistanceSqr

                for (dir in Direction.values()) {
                    mutable2.set(mutable, dir)
                    val distance = distanceSqrBetween(mutable2, end)
                    if (distance < nextDistance && canLeyline(world, mutable2) && chunkContains(chunkPos, mutable2)) {
                        nextDir = dir
                        nextDistance = distance
                    }
                }

                if (nextDir == null) {
                    break
                }

                mutable.move(nextDir)

                makeLeyline(world, mutable)

                if (mutable == end) {
                    break
                }
            }
        }
    }
}