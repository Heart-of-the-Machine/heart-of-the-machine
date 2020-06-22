package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.mojang.serialization.Codec
import net.minecraft.block.FacingBlock
import net.minecraft.block.PillarBlock
import net.minecraft.block.StairsBlock
import net.minecraft.nbt.CompoundTag
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructurePieceWithDimensions
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.StructureFeature
import net.minecraft.world.gen.feature.StructureFeature.StructureStartFactory
import java.util.*

/**
 * Structure Feature for the Nectere Portal.
 */
class NecterePortalFeature(config: Codec<DefaultFeatureConfig>) : StructureFeature<DefaultFeatureConfig>(config) {
    override fun getStructureStartFactory(): StructureStartFactory<DefaultFeatureConfig> {
        return StructureStartFactory { feature, chunkX, chunkZ, box, references, seed ->
            Start(feature, chunkX, chunkZ, box, references, seed)
        }
    }

    /**
     * Nectere Portal Structure Start.
     */
    class Start(
        feature: StructureFeature<DefaultFeatureConfig>,
        chunkX: Int,
        chunkZ: Int,
        box: BlockBox,
        references: Int,
        seed: Long
    ) : StructureStart<DefaultFeatureConfig>(feature, chunkX, chunkZ, box, references, seed) {
        override fun init(
            chunkGenerator: ChunkGenerator,
            structureManager: StructureManager,
            x: Int,
            z: Int,
            biome: Biome,
            featureConfig: DefaultFeatureConfig
        ) {
            children.add(Piece(random, x * 16, z * 16))
            setBoundingBoxFromChildren()
        }
    }

    /**
     * Singular Nectere Portal Structure Piece.
     */
    class Piece : StructurePieceWithDimensions {
        constructor(random: Random, x: Int, z: Int) : super(
            HotMStructurePieces.NECTERE_PORTAL,
            random,
            x,
            64,
            z,
            5,
            4,
            5
        )

        constructor(manager: StructureManager, tag: CompoundTag) : super(HotMStructurePieces.NECTERE_PORTAL, tag)

        override fun generate(
            world: ServerWorldAccess,
            structureAccessor: StructureAccessor,
            chunkGenerator: ChunkGenerator,
            random: Random,
            boundingBox: BlockBox,
            chunkPos: ChunkPos,
            blockPos: BlockPos
        ): Boolean {
            if (!method_14839(world, boundingBox, 0)) {
                return false
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

            addBlock(world, yGlowyPillar, 2, 0, 2, boundingBox)
            addBlock(world, yGlowyPillar, 2, 3, 2, boundingBox)

            addBlock(world, yGlowyPillar, 0, 1, 2, boundingBox)
            addBlock(world, yGlowyPillar, 0, 2, 2, boundingBox)
            addBlock(world, yGlowyPillar, 4, 1, 2, boundingBox)
            addBlock(world, yGlowyPillar, 4, 2, 2, boundingBox)
            addBlock(world, yGlowyPillar, 2, 1, 0, boundingBox)
            addBlock(world, yGlowyPillar, 2, 2, 0, boundingBox)
            addBlock(world, yGlowyPillar, 2, 1, 4, boundingBox)
            addBlock(world, yGlowyPillar, 2, 2, 4, boundingBox)

            addBlock(world, xGlowyPillar, 0, 0, 2, boundingBox)
            addBlock(world, xGlowyPillar, 1, 0, 2, boundingBox)
            addBlock(world, xGlowyPillar, 3, 0, 2, boundingBox)
            addBlock(world, xGlowyPillar, 4, 0, 2, boundingBox)
            addBlock(world, xGlowyPillar, 1, 3, 2, boundingBox)
            addBlock(world, xGlowyPillar, 3, 3, 2, boundingBox)

            addBlock(world, zGlowyPillar, 2, 0, 0, boundingBox)
            addBlock(world, zGlowyPillar, 2, 0, 1, boundingBox)
            addBlock(world, zGlowyPillar, 2, 0, 3, boundingBox)
            addBlock(world, zGlowyPillar, 2, 0, 4, boundingBox)
            addBlock(world, zGlowyPillar, 2, 3, 1, boundingBox)
            addBlock(world, zGlowyPillar, 2, 3, 3, boundingBox)

            addBlock(world, yPillar, 1, 0, 1, boundingBox)
            addBlock(world, yPillar, 3, 0, 1, boundingBox)
            addBlock(world, yPillar, 1, 0, 3, boundingBox)
            addBlock(world, yPillar, 3, 0, 3, boundingBox)

            addBlock(world, xPillar, 0, 0, 1, boundingBox)
            addBlock(world, xPillar, 0, 0, 3, boundingBox)
            addBlock(world, xPillar, 4, 0, 1, boundingBox)
            addBlock(world, xPillar, 4, 0, 3, boundingBox)

            addBlock(world, zPillar, 1, 0, 0, boundingBox)
            addBlock(world, zPillar, 3, 0, 0, boundingBox)
            addBlock(world, zPillar, 1, 0, 4, boundingBox)
            addBlock(world, zPillar, 3, 0, 4, boundingBox)

            addBlock(world, nStairs, 2, 3, 0, boundingBox)
            addBlock(world, sStairs, 2, 3, 4, boundingBox)
            addBlock(world, eStairs, 0, 3, 2, boundingBox)
            addBlock(world, wStairs, 4, 3, 2, boundingBox)

            addBlock(world, uPortal, 2, 1, 2, boundingBox)
            addBlock(world, dPortal, 2, 2, 2, boundingBox)

            fill(world, boundingBox, 0, 1, 1, 4, 2, 1)
            fill(world, boundingBox, 0, 1, 3, 4, 2, 3)
            fill(world, boundingBox, 1, 1, 0, 1, 2, 4)
            fill(world, boundingBox, 3, 1, 0, 3, 2, 4)

            return true
        }
    }
}