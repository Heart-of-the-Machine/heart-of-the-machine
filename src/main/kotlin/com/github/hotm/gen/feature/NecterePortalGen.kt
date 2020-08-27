package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.mixin.StructurePieceAccessor
import net.minecraft.block.*
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.Heightmap
import net.minecraft.world.WorldAccess
import java.util.*

object NecterePortalGen {
    const val PORTAL_OFFSET_X = 2
    const val PORTAL_OFFSET_Y = 1
    const val PORTAL_OFFSET_Z = 2

    fun portalPos(startPos: BlockPos): BlockPos {
        return startPos.add(PORTAL_OFFSET_X, PORTAL_OFFSET_Y, PORTAL_OFFSET_Z)
    }

    fun unPortalPos(portalPos: BlockPos): BlockPos {
        return portalPos.add(-PORTAL_OFFSET_X, -PORTAL_OFFSET_Y, -PORTAL_OFFSET_Z)
    }

    fun getPortalStructureY(world: WorldAccess, x: Int, z: Int, random: Random): Int {
        val worldHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z)
        return if (worldHeight < 4) {
            random.nextInt(124) + 4
        } else {
            worldHeight
        }
    }

    fun getPortalStructureX(chunkX: Int): Int {
        return chunkX.shl(4)
    }

    fun getPortalX(chunkX: Int): Int {
        return getPortalStructureX(chunkX) + PORTAL_OFFSET_X
    }

    fun getPortalStructureZ(chunkZ: Int): Int {
        return chunkZ.shl(4)
    }

    fun getPortalZ(chunkZ: Int): Int {
        return getPortalStructureZ(chunkZ) + PORTAL_OFFSET_Z
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
        fun addBlock(block: BlockState, x: Int, y: Int, z: Int) {
            var blockMut = block
            val blockPos = BlockPos(applyXTransform(x, z), applyYTransform(y), applyZTransform(x, z))
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

        addBlock(uPortal, PORTAL_OFFSET_X, PORTAL_OFFSET_Y, PORTAL_OFFSET_Z)
        addBlock(dPortal, PORTAL_OFFSET_X, PORTAL_OFFSET_Y + 1, PORTAL_OFFSET_Z)

        fill(0, 1, 1, 4, 2, 1)
        fill(0, 1, 3, 4, 2, 3)
        fill(1, 1, 0, 1, 2, 4)
        fill(3, 1, 0, 3, 2, 4)
    }
}