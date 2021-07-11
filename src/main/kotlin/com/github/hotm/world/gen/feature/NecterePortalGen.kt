package com.github.hotm.world.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.config.HotMBiomesConfig
import com.github.hotm.mixin.StructurePieceAccessor
import com.github.hotm.world.HotMBiomeData
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMPortalOffsets
import net.minecraft.block.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.Heightmap
import net.minecraft.world.WorldAccess
import java.util.*

object NecterePortalGen {
    private const val MIN_ROOF_HEIGHT = 32

    fun portalPos(chunkPos: ChunkPos): BlockPos {
        // FIXME: This just assumes the portal is at y: 64
        return BlockPos(getPortalX(chunkPos.x), 64, getPortalZ(chunkPos.z))
    }

    fun getPortalStructureY(world: WorldAccess, x: Int, z: Int, random: Random): Int {
        val surfaces = mutableListOf<Int>()
        val pos = BlockPos.Mutable(x, 0, z)
        var prevAir = world.isAir(pos)
        var roof = -1

        for (y in 1..250) {
            pos.y = y

            if (world.getBlockState(pos).block == Blocks.BEDROCK && y > MIN_ROOF_HEIGHT) {
                roof = y
                break
            }

            val air = world.isAir(pos)
            if (!prevAir && air) {
                surfaces.add(y)
            }
            prevAir = air
        }

        return when {
            surfaces.isEmpty() -> random.nextInt(
                if (roof > MIN_ROOF_HEIGHT) {
                    roof - 8
                } else {
                    124
                }
            ) + 4
            roof > MIN_ROOF_HEIGHT -> surfaces[random.nextInt(surfaces.size)]
            else -> world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z)
        }
    }

    fun getPortalStructureX(chunkX: Int): Int {
        return chunkX.shl(4)
    }

    fun getPortalX(chunkX: Int): Int {
        return HotMPortalOffsets.structure2PortalX(getPortalStructureX(chunkX))
    }

    fun getPortalStructureZ(chunkZ: Int): Int {
        return chunkZ.shl(4)
    }

    fun getPortalZ(chunkZ: Int): Int {
        return HotMPortalOffsets.structure2PortalZ(getPortalStructureZ(chunkZ))
    }

    fun generateForChunk(world: ServerWorld, pos: ChunkPos, random: Random) {
        if (world.registryKey != HotMDimensions.NECTERE_KEY) {
            val nectereWorld = HotMDimensions.getNectereWorld(world.server)
            HotMDimensions.getNonNecterePortalCoords(
                world.registryManager,
                world.registryKey,
                pos,
                { resX, resZ -> getPortalStructureY(world, resX, resZ, random) },
                nectereWorld
            ).filter { structurePos ->
                // Make sure the portal is in an enabled biome and not in a Nectere biome.
                val portalPos = HotMPortalOffsets.structure2PortalPos(structurePos)
                val biome = world.getBiomeKey(portalPos).orElse(null)

                biome != null
                        && !HotMBiomesConfig.CONFIG.necterePortalDenyBiomes!!.contains(biome.value.toString())
                        && !HotMBiomeData.getDataById().containsKey(biome)
                        && HotMDimensions.findNecterePortal(world, listOf(portalPos)) == null
            }.forEach { structurePos ->
                generate(world, structurePos)
            }
        }
    }

    fun generate(world: WorldAccess, pos: BlockPos) {
        generate(
            world,
            null,
            { x, _ -> x + pos.x },
            { y -> y + pos.y },
            { _, z -> z + pos.z },
            BlockMirror.LEFT_RIGHT,
            BlockRotation.NONE
        )
    }

    fun generate(
        world: WorldAccess,
        boundingBox: BlockBox?,
        applyXTransform: (Int, Int) -> Int,
        applyYTransform: (Int) -> Int,
        applyZTransform: (Int, Int) -> Int,
        mirror: BlockMirror,
        rotation: BlockRotation
    ) {
        fun addBlock(block: BlockState, blockPos: BlockPos) {
            var blockMut = block
            if (boundingBox == null || boundingBox.contains(blockPos)) {
                if (mirror != BlockMirror.NONE) {
                    blockMut = blockMut.mirror(mirror)
                }
                if (rotation != BlockRotation.NONE) {
                    blockMut = blockMut.rotate(rotation)
                }
                world.setBlockState(blockPos, blockMut, 2)
                val fluidState = world.getFluidState(blockPos)
                if (!fluidState.isEmpty) {
                    world.fluidTickScheduler.schedule(blockPos, fluidState.fluid, 0)
                }
                if (StructurePieceAccessor.getBlocksNeedingPostProcessing().contains(blockMut.block)) {
                    world.getChunk(blockPos).markBlockForPostProcessing(blockPos)
                }
            }
        }

        fun addBlock(block: BlockState, x: Int, y: Int, z: Int) {
            addBlock(block, BlockPos(applyXTransform(x, z), applyYTransform(y), applyZTransform(x, z)))
        }

        fun fill(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) {
            for (j in minY..maxY) {
                for (k in minX..maxX) {
                    for (l in minZ..maxZ) {
                        addBlock(Blocks.AIR.defaultState, k, j, l)
                    }
                }
            }
        }

        val xGlowyPillar = HotMBlocks.GLOWY_OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.X)
        val yGlowyPillar = HotMBlocks.GLOWY_OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.Y)
        val zGlowyPillar = HotMBlocks.GLOWY_OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.Z)
        val xPillar = HotMBlocks.OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.X)
        val yPillar = HotMBlocks.OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.Y)
        val zPillar = HotMBlocks.OBELISK_PART.defaultState.with(PillarBlock.AXIS, Direction.Axis.Z)
        val nStairs = HotMBlocks.THINKING_STONE_BRICK_STAIRS.defaultState.with(StairsBlock.FACING, Direction.NORTH)
        val sStairs = HotMBlocks.THINKING_STONE_BRICK_STAIRS.defaultState.with(StairsBlock.FACING, Direction.SOUTH)
        val eStairs = HotMBlocks.THINKING_STONE_BRICK_STAIRS.defaultState.with(StairsBlock.FACING, Direction.EAST)
        val wStairs = HotMBlocks.THINKING_STONE_BRICK_STAIRS.defaultState.with(StairsBlock.FACING, Direction.WEST)
        val uPortal = HotMBlocks.NECTERE_PORTAL.defaultState.with(FacingBlock.FACING, Direction.UP)
        val dPortal = HotMBlocks.NECTERE_PORTAL.defaultState.with(FacingBlock.FACING, Direction.DOWN)

        addBlock(yGlowyPillar, 2, 0, 2)
        addBlock(yGlowyPillar, 2, 3, 2)

        addBlock(yGlowyPillar, 0, 1, 2)
        addBlock(yGlowyPillar, 0, 2, 2)
        addBlock(yGlowyPillar, 4, 1, 2)
        addBlock(yGlowyPillar, 4, 2, 2)
        addBlock(yGlowyPillar, 2, 1, 0)
        addBlock(yGlowyPillar, 2, 2, 0)
        addBlock(yGlowyPillar, 2, 1, 4)
        addBlock(yGlowyPillar, 2, 2, 4)

        addBlock(xGlowyPillar, 0, 0, 2)
        addBlock(xGlowyPillar, 1, 0, 2)
        addBlock(xGlowyPillar, 3, 0, 2)
        addBlock(xGlowyPillar, 4, 0, 2)
        addBlock(xGlowyPillar, 1, 3, 2)
        addBlock(xGlowyPillar, 3, 3, 2)

        addBlock(zGlowyPillar, 2, 0, 0)
        addBlock(zGlowyPillar, 2, 0, 1)
        addBlock(zGlowyPillar, 2, 0, 3)
        addBlock(zGlowyPillar, 2, 0, 4)
        addBlock(zGlowyPillar, 2, 3, 1)
        addBlock(zGlowyPillar, 2, 3, 3)

        addBlock(yPillar, 1, 0, 1)
        addBlock(yPillar, 3, 0, 1)
        addBlock(yPillar, 1, 0, 3)
        addBlock(yPillar, 3, 0, 3)

        addBlock(xPillar, 0, 0, 1)
        addBlock(xPillar, 0, 0, 3)
        addBlock(xPillar, 4, 0, 1)
        addBlock(xPillar, 4, 0, 3)

        addBlock(zPillar, 1, 0, 0)
        addBlock(zPillar, 3, 0, 0)
        addBlock(zPillar, 1, 0, 4)
        addBlock(zPillar, 3, 0, 4)

        addBlock(nStairs, 2, 3, 0)
        addBlock(sStairs, 2, 3, 4)
        addBlock(eStairs, 0, 3, 2)
        addBlock(wStairs, 4, 3, 2)

        addBlock(uPortal, HotMPortalOffsets.transform2PortalPos(applyXTransform, applyYTransform, applyZTransform))
        addBlock(dPortal, HotMPortalOffsets.transform2PortalPos(applyXTransform, applyYTransform, applyZTransform))

        fill(0, 1, 1, 4, 2, 1)
        fill(0, 1, 3, 4, 2, 3)
        fill(1, 1, 0, 1, 2, 4)
        fill(3, 1, 0, 3, 2, 4)
    }
}