package com.github.hotm.mod.world.gen

import java.util.Random
import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.blockentity.NecterePortalSpawnerBlockEntity
import com.github.hotm.mod.world.HotMDimensions
import com.github.hotm.mod.world.HotMPortalFinders
import com.github.hotm.mod.world.HotMPortalGenPositions
import com.github.hotm.mod.world.HotMPortalOffsets
import com.github.hotm.mod.world.biome.NecterePortalData
import kotlin.jvm.optionals.getOrNull
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FacingBlock
import net.minecraft.block.PillarBlock
import net.minecraft.block.StairsBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.WorldAccess
import net.minecraft.world.tick.OrderedTick

object HotMPortalGen {

    /**
     * Makes sure all NecterePortalSpawnerBlockEntities in the destination chunks have generated their receiving portals.
     */
    fun pregenPortals(world: ServerWorld, newPoses: List<BlockPos>) {
        // look for and run nectere portal spawner block entities
        val checked = mutableSetOf<ChunkPos>()

        for (pos in newPoses) {
            val chunkPos = ChunkPos(pos)
            if (!checked.contains(chunkPos)) {
                checked.add(chunkPos)

                pregenPortal(world, chunkPos)
            }
        }
    }

    /**
     * Makes sure the NecterePortalSpawnerBlockEntity in the destination chunk has generated its receiving portals.
     */
    fun pregenPortal(world: ServerWorld, chunkPos: ChunkPos) {
        val spawnerPos = HotMPortalGenPositions.getPortalSpawnerPos(world, chunkPos)

        (world.getBlockEntity(spawnerPos) as? NecterePortalSpawnerBlockEntity)?.spawn()
    }

    /**
     * Creates a Nectere portal at an optimal position among a list of destination positions and returns the location of
     * that portal.
     *
     * This method expects all biome and portal validity checking to be done beforehand.
     */
    fun createNecterePortal(world: StructureWorldAccess, newPoses: List<BlockPos>): BlockPos {
        val rand = Random()
        val portalXZ = newPoses[rand.nextInt(newPoses.size)]

        val portalPos = HotMPortalGenPositions.findPortalPos(world, portalXZ.x, portalXZ.z)
        val structurePos = HotMPortalOffsets.portal2StructurePos(portalPos)

        generate(world, structurePos)

        return portalPos
    }

    /**
     * Retro-generates the "nearest" Nectere portal.
     */
//    fun retrogenNonNectereSidePortal(
//        currentWorld: ServerWorld,
//        currentPos: BlockPos,
//        radius: Int
//    ): RetrogenPortalResult {
//        return HotMPortalFinders.locateNonNectereSidePortal(currentWorld, currentPos, radius, false)
//            ?.let { structurePos ->
//                val portalPos = HotMPortalOffsets.structure2PortalPos(structurePos)
//                val foundPos = HotMPortalFinders.findNecterePortal(currentWorld, listOf(portalPos))
//                if (foundPos == null) {
//                    val newPortalPos =
//                        HotMPortalGenPositions.findMaybeValidNonNecterePortalPos(currentWorld, portalPos.x, portalPos.z)
//                    val newStructurePos = HotMPortalOffsets.portal2StructurePos(newPortalPos)
//                    generate(currentWorld, newStructurePos)
//
//                    RetrogenPortalResult.Generated(newStructurePos)
//                } else {
//                    RetrogenPortalResult.Found(HotMPortalOffsets.portal2StructurePos(foundPos))
//                }
//            } ?: RetrogenPortalResult.Failure
//    }

    /**
     * Generates all non-nectere side portals for a given chunk.
     */
    fun generateNonNectereSideForChunk(world: ServerWorld, pos: ChunkPos) {
        if (world.registryKey != HotMDimensions.NECTERE_KEY) {
            val nectereWorld = HotMDimensions.getNectere(world.server)
            HotMPortalFinders.getNonNecterePortalPlacementsForChunk(
                world.registryKey,
                pos,
                nectereWorld
            ).forEach { placement ->
                val portalXZ = placement.portalXZ
                val portalPos = HotMPortalGenPositions.findPortalPos(world, portalXZ.x, portalXZ.z)

                // Make sure the portal is in an enabled biome and not in a Nectere biome.
                val biome = world.getBiome(portalPos).key.getOrNull()

                if (biome != null
                    && !NecterePortalData.BIOMES_BY_ID.containsKey(biome)
                    && HotMPortalFinders.findNonNecterePortal(world, portalPos, placement.portalHolder) == null
                ) {
                    val structurePos = HotMPortalOffsets.portal2StructurePos(portalPos)
                    generate(world, structurePos)
                }
            }
        }
    }

    /**
     * Generates a nectere side portal that should be part of a portal structure.
     *
     * @return true if the portal was successfully generated, false otherwise.
     */
    fun generateNectereSideForStructure(
        world: WorldAccess,
        boundingBox: BlockBox?,
        applyXTransform: (Int, Int) -> Int,
        applyYTransform: (Int) -> Int,
        applyZTransform: (Int, Int) -> Int,
        mirror: BlockMirror,
        rotation: BlockRotation
    ): Boolean {
        generate(world, boundingBox, applyXTransform, applyYTransform, applyZTransform, mirror, rotation)

        return true
    }

    /**
     * Generates a nectere portal in a given world at a given location.
     */
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

    /**
     * Generates a nectere portal in a given world at a given location. This generates only the part within the bounding
     * box. This also applies the given x, y, and z transformations as well as the mirror and rotation transformations.
     */
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
            if (boundingBox == null || boundingBox.isInside(blockPos)) {
                if (mirror != BlockMirror.NONE) {
                    blockMut = blockMut.mirror(mirror)
                }
                if (rotation != BlockRotation.NONE) {
                    blockMut = blockMut.rotate(rotation)
                }
                world.setBlockState(blockPos, blockMut, 2)
                val fluidState = world.getFluidState(blockPos)
                if (!fluidState.isEmpty) {
                    world.fluidTickScheduler.scheduleTick(OrderedTick.create(fluidState.fluid, blockPos))
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
        addBlock(dPortal, HotMPortalOffsets.transform2PortalPos(applyXTransform, applyYTransform, applyZTransform).up())

        fill(0, 1, 1, 4, 2, 1)
        fill(0, 1, 3, 4, 2, 3)
        fill(1, 1, 0, 1, 2, 4)
        fill(3, 1, 0, 3, 2, 4)
    }

    /**
     * The possible results of portal retro-generation.
     */
    sealed class RetrogenPortalResult {
        object Failure : RetrogenPortalResult()
        data class Found(val blockPos: BlockPos) : RetrogenPortalResult()
        data class Generated(val blockPos: BlockPos) : RetrogenPortalResult()
    }
}
