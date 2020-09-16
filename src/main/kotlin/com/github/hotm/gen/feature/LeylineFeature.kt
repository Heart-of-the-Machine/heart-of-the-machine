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
import java.util.stream.Collectors
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
                for (dir in DirectionUtils.horizontals()) {
                    val dests = Stream.concat(
                        getBoundaries(world.seed, chunkGenerator, chunkPos, dir),
                        getNeighborBoundaries(world.seed, chunkGenerator, chunkPos, dir)
                    ).collect(Collectors.toSet())
                    val paths = findBlockPaths(world, source, chunkPos, dests.toSet())
                    for (path in paths) {
                        makeLeylinePath(world, path)
                    }
                }
            }
        }

        return generated
    }

    companion object {
        const val MAX_LEYLINE_HEIGHT = 200
        const val SOURCE_INDEX = 538872585
        const val SOURCE_STEP = 1005715095
        const val BORDER_INDEX = 1456570221
        const val BORDER_STEP = -1614785170
        const val DIRECTION_INDEX = 94861301
        const val MAX_LEYLINES_PER_CHUNK = 2
        const val MAX_BOUNDARIES_PER_CHUNNK_SIDE = 3
        const val MAX_LEYLINE_LENGTH = 64

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

        fun chunkContains(chunkPos: ChunkPos, blockPos: BlockPos): Boolean {
            return blockPos.x <= chunkPos.endX && blockPos.x >= chunkPos.startX
                    && blockPos.z <= chunkPos.endZ && blockPos.z >= chunkPos.startZ
        }

        fun findBlockPaths(
            world: StructureWorldAccess,
            start: BlockPos,
            chunkPos: ChunkPos,
            ends: Set<BlockPos>
        ): List<BlockPathNode> {
            val toFind = ends.toMutableSet()
            val traversed = mutableSetOf<BlockPos>()
            val paths = mutableListOf<BlockPathNode>()
            val layers = mutableListOf<Map<BlockPos, BlockPathNode>>()
            layers.add(mapOf(start to BlockPathNode(start, null)))
            var layer = 1

            while (toFind.isNotEmpty()) {
                val prevLayer = layers[layer - 1]

                if (prevLayer.isEmpty()) {
                    break
                }

                if (layers.size > MAX_LEYLINE_LENGTH) {
                    break
                }

                val nextLayer = mutableMapOf<BlockPos, BlockPathNode>()
                layers.add(nextLayer)

                for (posPath in prevLayer) {
                    for (dir in Direction.values()) {
                        val pos = posPath.key.offset(dir)
                        if (chunkContains(chunkPos, pos) && canLeyline(world, pos) && !traversed.contains(pos)) {
                            val path = BlockPathNode(pos, posPath.value)

                            if (toFind.contains(pos)) {
                                paths.add(path)
                                toFind.remove(pos)
                            }

                            traversed.add(pos)
                            nextLayer[pos] = path
                        }
                    }
                }

                layer++
            }

            return paths
        }

        fun makeLeylinePath(world: StructureWorldAccess, path: BlockPathNode) {
            var cur: BlockPathNode? = path
            while (cur != null) {
                makeLeyline(world, cur.pos)
                cur = cur.prev
            }
        }
    }

    data class BlockPathNode(val pos: BlockPos, val prev: BlockPathNode?)
}