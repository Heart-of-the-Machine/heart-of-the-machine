package com.github.hotm.world.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.blocks.Leylineable
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.HeightLimitView
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream

class LeylineFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    override fun generate(
        ctx: FeatureContext<DefaultFeatureConfig>
    ): Boolean {
        val world = ctx.world
        val blockPos = ctx.origin
        val chunkGenerator = ctx.generator
        var generated = false

        if (isGenChunk(blockPos)) {
            val regions = RegionPos.regionsForChunk(blockPos)

            for (regionPos in regions) {
                val sources = regionPos.genSources(world.seed, chunkGenerator, world)

                for (source in sources) {
                    if (canLeyline(world, source)) {
                        generated = true
                        makeLeyline(world, source)

                        for (dir in Direction.values()) {
                            val dests = regionPos.genBoundaries(world.seed, chunkGenerator, dir, world)

                            for (dest in dests) {
                                makeLeylineTo(world, source, regionPos, dest)
                            }
                        }
                    }
                }
            }
        }

        return generated
    }

    companion object {
        const val MAX_LEYLINE_HEIGHT = 192
        const val SOURCE_INDEX = 538872585
        const val SOURCE_STEP = 1005715095
        const val BORDER_INDEX = 1456570221
        const val BORDER_STEP = -1614785170
        const val DIRECTION_INDEX = 94861301
        const val CHUNKS_PER_REGION = 3
        const val BLOCKS_PER_REGION = CHUNKS_PER_REGION shl 4
        const val MAX_LEYLINES_PER_REGION = 1
        const val MAX_BOUNDARIES_PER_REGION_SIDE = 1
        const val MAX_LEYLINE_LENGTH = 64
        const val MAX_LEYLINE_LENGTH_SQR = MAX_LEYLINE_LENGTH * MAX_LEYLINE_LENGTH

        fun isGenChunk(blockPos: BlockPos): Boolean {
            val chunkX = blockPos.x shr 4
            val chunkZ = blockPos.z shr 4
            return MathHelper.floorMod(chunkX, CHUNKS_PER_REGION) == CHUNKS_PER_REGION / 2
                    && MathHelper.floorMod(chunkZ, CHUNKS_PER_REGION) == CHUNKS_PER_REGION / 2
        }

        fun makeLeyline(world: StructureWorldAccess, blockPos: BlockPos) {
            val state = world.getBlockState(blockPos)
            (state.block as? Leylineable)?.let { world.setBlockState(blockPos, it.getLeyline(state), 3) }
        }

        fun canLeyline(world: StructureWorldAccess, blockPos: BlockPos): Boolean {
            val block = world.getBlockState(blockPos).block
            return block is Leylineable || HotMBlocks.isLeyline(block)
        }

        fun distanceSqrBetween(a: BlockPos, b: BlockPos): Int {
            val xDif = a.x - b.x
            val yDif = a.y - b.y
            val zDif = a.z - b.z
            return xDif * xDif + yDif * yDif + zDif * zDif
        }

        fun makeLeylineTo(world: StructureWorldAccess, start: BlockPos, regionPos: RegionPos, end: BlockPos) {
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
                    if (distance < nextDistance && canLeyline(world, mutable2) && regionPos.isBlockWithin(mutable2)) {
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

    data class RegionPos(val x: Int, val y: Int, val z: Int) {
        companion object {
            fun fromBlockPos(blockPos: BlockPos): RegionPos {
                return RegionPos(
                    MathHelper.floorDiv(blockPos.x, BLOCKS_PER_REGION),
                    MathHelper.floorDiv(blockPos.y, BLOCKS_PER_REGION),
                    MathHelper.floorDiv(blockPos.z, BLOCKS_PER_REGION)
                )
            }

            fun regionsForChunk(blockPos: BlockPos): Stream<RegionPos> {
                return IntStream.range(0, MAX_LEYLINE_HEIGHT / BLOCKS_PER_REGION).mapToObj { y ->
                    RegionPos(
                        MathHelper.floorDiv(blockPos.x, BLOCKS_PER_REGION),
                        y,
                        MathHelper.floorDiv(blockPos.z, BLOCKS_PER_REGION)
                    )
                }
            }
        }

        val startX = x * BLOCKS_PER_REGION
        val startY = y * BLOCKS_PER_REGION
        val startZ = z * BLOCKS_PER_REGION
        val endX = (x + 1) * BLOCKS_PER_REGION - 1
        val endY = (y + 1) * BLOCKS_PER_REGION - 1
        val endZ = (z + 1) * BLOCKS_PER_REGION - 1

        val isHeightValid = y >= 0 && y < MAX_LEYLINE_HEIGHT / BLOCKS_PER_REGION
        val isBottom = y == 0
        val isTop = (y + 2) * BLOCKS_PER_REGION - 1 >= MAX_LEYLINE_HEIGHT

        fun getRandom(worldSeed: Long, index: Int, step: Int): ChunkRandom {
            val random = ChunkRandom()
            random.setSeed(worldSeed)
            val xMult = random.nextLong() or 1L
            val yMult = random.nextLong() or 1L
            val zMult = random.nextLong() or 1L
            val regMult = BLOCKS_PER_REGION.toLong()
            val populationSeed =
                (x.toLong() * regMult * xMult + y.toLong() * regMult * yMult + z.toLong() * regMult * zMult) xor worldSeed
            random.setDecoratorSeed(populationSeed, index, step)
            return random
        }

        fun isBlockWithin(pos: BlockPos): Boolean {
            return pos.x >= x * BLOCKS_PER_REGION && pos.x < (x + 1) * BLOCKS_PER_REGION
                    && pos.y >= y * BLOCKS_PER_REGION && pos.y < (y + 1) * BLOCKS_PER_REGION
                    && pos.z >= z * BLOCKS_PER_REGION && pos.z < (z + 1) * BLOCKS_PER_REGION
        }

        fun nextRegion(direction: Direction): RegionPos? {
            return when (direction) {
                Direction.DOWN -> if (isBottom) null else RegionPos(x, y - 1, z)
                Direction.UP -> if (isTop) null else RegionPos(x, y + 1, z)
                Direction.NORTH -> RegionPos(x, y, z - 1)
                Direction.SOUTH -> RegionPos(x, y, z + 1)
                Direction.WEST -> RegionPos(x - 1, y, z)
                Direction.EAST -> RegionPos(x + 1, y, z)
            }
        }

        fun genSources(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            heightLimitView: HeightLimitView
        ): Stream<BlockPos> {
            val random = getRandom(worldSeed, SOURCE_INDEX, SOURCE_STEP)

            return IntStream.range(0, MAX_LEYLINES_PER_REGION).mapToObj {
                BlockPos(
                    random.nextInt(BLOCKS_PER_REGION) + startX,
                    random.nextInt(BLOCKS_PER_REGION) + startY,
                    random.nextInt(BLOCKS_PER_REGION) + startZ
                )
            }.filter {
                val sample = chunkGenerator.getColumnSample(it.x, it.z, heightLimitView)
                sample.getState(it).block is Leylineable
            }
        }

        private fun genRegionBoundaries(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            direction: Direction,
            heightLimitView: HeightLimitView
        ): Stream<BlockPos> {
            val random =
                getRandom(worldSeed, BORDER_INDEX + DIRECTION_INDEX * direction.ordinal, BORDER_STEP)

            return when (direction) {
                Direction.DOWN -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        random.nextInt(BLOCKS_PER_REGION) + startX,
                        startY,
                        random.nextInt(BLOCKS_PER_REGION) + startZ
                    )
                }
                Direction.UP -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        random.nextInt(BLOCKS_PER_REGION) + startX,
                        endY,
                        random.nextInt(BLOCKS_PER_REGION) + startZ
                    )
                }
                Direction.NORTH -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        random.nextInt(BLOCKS_PER_REGION) + startX,
                        random.nextInt(BLOCKS_PER_REGION) + startY,
                        startZ
                    )
                }
                Direction.SOUTH -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        random.nextInt(BLOCKS_PER_REGION) + startX,
                        random.nextInt(BLOCKS_PER_REGION) + startY,
                        endZ
                    )
                }
                Direction.WEST -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        startX,
                        random.nextInt(BLOCKS_PER_REGION) + startY,
                        random.nextInt(BLOCKS_PER_REGION) + startZ
                    )
                }
                Direction.EAST -> IntStream.range(0, MAX_BOUNDARIES_PER_REGION_SIDE).mapToObj {
                    BlockPos(
                        endX,
                        random.nextInt(BLOCKS_PER_REGION) + startY,
                        random.nextInt(16) + startZ
                    )
                }
            }.filter {
                val sample = chunkGenerator.getColumnSample(it.x, it.z, heightLimitView)
                sample.getState(it).block is Leylineable
            }
        }

        private fun genNeighborBoundaries(
            worldSeed: Long,
            chunkGenerator: ChunkGenerator,
            direction: Direction,
            heightLimitView: HeightLimitView
        ): Stream<BlockPos> {
            val opposite = direction.opposite
            return nextRegion(direction)?.let { region ->
                region.genRegionBoundaries(worldSeed, chunkGenerator, opposite, heightLimitView).map { it.offset(opposite) }
            } ?: Stream.empty()
        }

        fun genBoundaries(worldSeed: Long, chunkGenerator: ChunkGenerator, direction: Direction, heightLimitView: HeightLimitView): Stream<BlockPos> {
            return when (direction) {
                Direction.DOWN -> if (isBottom) Stream.empty() else Stream.concat(
                    genRegionBoundaries(worldSeed, chunkGenerator, direction, heightLimitView),
                    genNeighborBoundaries(worldSeed, chunkGenerator, direction, heightLimitView)
                )
                Direction.UP -> if (isTop) Stream.empty() else Stream.concat(
                    genRegionBoundaries(worldSeed, chunkGenerator, direction, heightLimitView),
                    genNeighborBoundaries(worldSeed, chunkGenerator, direction, heightLimitView)
                )
                else -> Stream.concat(
                    genRegionBoundaries(worldSeed, chunkGenerator, direction, heightLimitView),
                    genNeighborBoundaries(worldSeed, chunkGenerator, direction, heightLimitView)
                )
            }
        }
    }
}