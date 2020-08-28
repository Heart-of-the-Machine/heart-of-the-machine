package com.github.hotm.gen.feature

import com.github.hotm.HotMConfig
import com.github.hotm.gen.HotMDimensions
import com.github.hotm.gen.biome.NectereBiome
import com.github.hotm.mixin.StructurePieceAccessor
import com.github.hotm.util.WorldUtils
import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructurePieceWithDimensions
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.StructureFeature
import net.minecraft.world.gen.feature.StructureFeature.StructureStartFactory
import java.util.*

/**
 * Structure Feature for the Nectere Portal.
 */
class NecterePortalStructureFeature(config: Codec<DefaultFeatureConfig>) :
    StructureFeature<DefaultFeatureConfig>(config) {
    companion object {
        private fun checkBiomes(nonNectereWorld: ServerWorld, nonNecterePos: BlockPos): Boolean {
            val structurePos = NecterePortalGen.unPortalPos(nonNecterePos)
            val biomeId = Registry.BIOME.getId(
                nonNectereWorld.chunkManager.chunkGenerator.biomeSource.getBiomeForNoiseGen(
                    structurePos.x shr 2,
                    structurePos.y shr 2,
                    structurePos.z shr 2
                )
            )

            return biomeId != null && !HotMConfig.CONFIG.necterePortalWorldGenBlacklistBiomes!!.contains(biomeId.toString())
        }

        private fun checkExactBiomes(nonNectereWorld: ServerWorld, nonNecterePos: BlockPos): Boolean {
            val structurePos = NecterePortalGen.unPortalPos(nonNecterePos)
            val biomeId = Registry.BIOME.getId(nonNectereWorld.getBiome(structurePos))

            return biomeId != null && !HotMConfig.CONFIG.necterePortalWorldGenBlacklistBiomes!!.contains(biomeId.toString())
        }
    }

    override fun getStructureStartFactory(): StructureStartFactory<DefaultFeatureConfig> {
        return StructureStartFactory { feature, chunkX, chunkZ, box, references, seed ->
            Start(feature, chunkX, chunkZ, box, references, seed)
        }
    }

    /**
     * Make sure Nectere portal structures only spawn in NectereBiomes that are portalable.
     */
    override fun shouldStartAt(
        chunkGenerator: ChunkGenerator,
        biomeSource: BiomeSource,
        seed: Long,
        chunkRandom: ChunkRandom,
        chunkX: Int,
        chunkZ: Int,
        biome: Biome,
        chunkPos: ChunkPos,
        featureConfig: DefaultFeatureConfig
    ): Boolean {
        val portalBiome = biomeSource.getBiomeForNoiseGen(
            NecterePortalGen.getPortalX(chunkX) shr 2,
            64 shr 2,
            NecterePortalGen.getPortalZ(chunkZ) shr 2
        )
        return portalBiome is NectereBiome && portalBiome.isPortalable
    }

    fun locateNonNectereSidePortal(
        nectereWorld: WorldView,
        structureAccessor: StructureAccessor,
        blockPos: BlockPos,
        maxRadius: Int,
        skipExistingChunks: Boolean,
        seed: Long,
        structureConfig: StructureConfig,
        biome: NectereBiome,
        nonNectereWorld: ServerWorld
    ): BlockPos? {
        val spacing = structureConfig.spacing
        val chunkX = blockPos.x shr 4
        val chunkZ = blockPos.z shr 4
        var curRadius = 0
        val chunkRandom = ChunkRandom()

        while (curRadius <= maxRadius) {
            for (structX in -curRadius..curRadius) {
                val xBorder = structX == -curRadius || structX == curRadius
                for (structZ in -curRadius..curRadius) {
                    val zBorder = structZ == -curRadius || structZ == curRadius
                    if (xBorder || zBorder) {
                        val curChunkX = chunkX + spacing * structX
                        val curChunkZ = chunkZ + spacing * structZ

                        val chunkPos: ChunkPos = method_27218(structureConfig, seed, chunkRandom, curChunkX, curChunkZ)

                        val chunk = nectereWorld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS)
                        val structureStart =
                            structureAccessor.getStructureStart(
                                ChunkSectionPos.from(chunk.pos, 0),
                                this,
                                chunk
                            )

                        if (structureStart != null && structureStart.hasChildren()) {
                            val portalPos = NecterePortalGen.portalPos(structureStart.pos)

                            if (biome == nectereWorld.getBiome(portalPos)) {
                                // Don't locate portals in biomes that won't generate portals in the first place
                                val nonNecterePos =
                                    HotMDimensions.getBaseCorrespondingNonNectereCoords(nectereWorld, portalPos)
                                if (nonNecterePos != null && checkExactBiomes(nonNectereWorld, nonNecterePos)) {
                                    val nonNectereStructurePos = NecterePortalGen.unPortalPos(nonNecterePos)

                                    if (skipExistingChunks && structureStart.isInExistingChunk) {
                                        structureStart.incrementReferences()
                                        return nonNectereStructurePos
                                    }

                                    if (!skipExistingChunks) {
                                        return nonNectereStructurePos
                                    }
                                }
                            }
                        }

                        if (curRadius == 0) {
                            break
                        }
                    }
                }

                if (curRadius == 0) {
                    break
                }
            }

            ++curRadius
        }

        return null
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
            children.add(
                Piece(
                    random,
                    NecterePortalGen.getPortalStructureX(x),
                    NecterePortalGen.getPortalStructureZ(z)
                )
            )
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

            // Make sure we aren't generating in an area connected to a blacklisted biome
            val serverWorld = WorldUtils.getServerWorld(world) ?: return false
            val portalPos = BlockPos(
                applyXTransform(NecterePortalGen.PORTAL_OFFSET_X, NecterePortalGen.PORTAL_OFFSET_Z),
                applyYTransform(NecterePortalGen.PORTAL_OFFSET_Y),
                applyZTransform(NecterePortalGen.PORTAL_OFFSET_X, NecterePortalGen.PORTAL_OFFSET_Z)
            )
            val otherWorld = HotMDimensions.getCorrespondingNonNectereWorld(serverWorld, portalPos) ?: return false
            val otherPoses = HotMDimensions.getBaseCorrespondingNonNectereCoords(serverWorld, portalPos)

            if (otherPoses == null || !checkBiomes(otherWorld, otherPoses)) {
                return false
            }

            @Suppress("cast_never_succeeds")
            val accessed = this as StructurePieceAccessor

            NecterePortalGen.generate(
                world,
                boundingBox,
                this::applyXTransform,
                this::applyYTransform,
                this::applyZTransform,
                accessed.mirror,
                accessed.rotation
            )

            return true
        }
    }
}