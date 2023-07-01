package com.github.hotm.mod.world.gen.structure

import com.github.hotm.mod.world.gen.HotMPortalGen
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtCompound
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructurePiecesCollector
import net.minecraft.structure.StructureType
import net.minecraft.structure.piece.StructurePieceWithDimensions
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.StructureFeature
import java.util.Optional

class NecterePortalStructureFeature(structureSettings: StructureSettings) : StructureFeature(structureSettings) {
    companion object {
        val CODEC: Codec<NecterePortalStructureFeature> = simpleCodec(::NecterePortalStructureFeature)
    }

    override fun findGenerationPos(context: GenerationContext): Optional<GenerationStub> =
        Optional.of(GenerationStub(context.chunkPos.startPos) {
            addStart(
                it,
                context
            )
        })

    override fun getType(): StructureType<*> = HotMStructures.NECTERE_PORTAL_TYPE

    private fun addStart(collector: StructurePiecesCollector, context: GenerationContext) {
        val chunkPos = context.chunkPos
        collector.addPiece(Piece(chunkPos.startX, 64, chunkPos.startZ))
    }

    class Piece : StructurePieceWithDimensions {
        constructor(x: Int, y: Int, z: Int) : super(
            HotMStructurePieces.NECTERE_PORTAL,
            x,
            y,
            z,
            5,
            4,
            5,
            Direction.NORTH
        )

        constructor(nbtCompound: NbtCompound) : super(
            HotMStructurePieces.NECTERE_PORTAL,
            nbtCompound
        )

        override fun generate(
            world: StructureWorldAccess, structureManager: StructureManager, chunkGenerator: ChunkGenerator,
            random: RandomGenerator, boundingBox: BlockBox, chunkPos: ChunkPos, pos: BlockPos
        ) {
            // TODO: investigate better ways to set the height
            if (!adjustToAverageHeight(world, boundingBox, 0)) return

            HotMPortalGen.generate(
                world,
                boundingBox,
                this::applyXTransform,
                this::applyYTransform,
                this::applyZTransform,
                mirror,
                rotation
            )
        }
    }
}
