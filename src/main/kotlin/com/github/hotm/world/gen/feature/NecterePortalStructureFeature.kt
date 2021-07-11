package com.github.hotm.world.gen.feature

import com.github.hotm.config.HotMBiomesConfig
import com.github.hotm.mixin.StructurePieceAccessor
import com.github.hotm.util.WorldUtils
import com.github.hotm.world.biome.HotMBiomeData
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMPortalOffsets
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructurePieceWithDimensions
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.HeightLimitView
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.WorldAccess
import net.minecraft.world.biome.Biome
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
// TODO: Move all of this generation mechanism to the BlockEntity-based system to prevent worldgen deadlocks.
class NecterePortalStructureFeature(config: Codec<DefaultFeatureConfig>) :
    StructureFeature<DefaultFeatureConfig>(config) {
    companion object {
        private fun checkBiomes(nonNectereWorld: ServerWorld, nonNecterePos: BlockPos): Boolean {
            val biomeId = nonNectereWorld.registryManager[Registry.BIOME_KEY].getId(
                nonNectereWorld.chunkManager.chunkGenerator.biomeSource.getBiomeForNoiseGen(
                    nonNecterePos.x shr 2,
                    nonNecterePos.y shr 2,
                    nonNecterePos.z shr 2
                )
            )

            return biomeId != null && !HotMBiomesConfig.CONFIG.necterePortalDenyBiomes!!.contains(
                biomeId.toString()
            )
        }

        private fun checkExactBiomes(nonNectereWorld: ServerWorld, nonNecterePos: BlockPos): Boolean {
            val biomeId = nonNectereWorld.getBiomeKey(nonNecterePos).orElse(null)?.value

            return biomeId != null && !HotMBiomesConfig.CONFIG.necterePortalDenyBiomes!!.contains(
                biomeId.toString()
            )
        }
    }

    override fun getStructureStartFactory(): StructureStartFactory<DefaultFeatureConfig> {
        return StructureStartFactory { feature, pos, references, seed ->
            Start(feature, pos, references, seed)
        }
    }

    // TODO: Move this somewhere else
    fun locateNonNectereSidePortal(
        nectereWorld: WorldAccess,
        structureAccessor: StructureAccessor,
        blockPos: BlockPos,
        maxRadius: Int,
        skipExistingChunks: Boolean,
        seed: Long,
        structureConfig: StructureConfig,
        biomeKey: RegistryKey<Biome>,
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

                        val chunkPos: ChunkPos = getStartChunk(structureConfig, seed, chunkRandom, curChunkX, curChunkZ)

                        val chunk = nectereWorld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS)
                        val structureStart =
                            structureAccessor.getStructureStart(
                                ChunkSectionPos.from(chunk.pos, 0),
                                this,
                                chunk
                            )

                        if (structureStart != null && structureStart.hasChildren()) {
                            // TODO: completely restructure this mechanism to generate portals on first tick
                            val portalPos = NecterePortalGen.portalPos(structureStart.pos)

                            if (biomeKey == nectereWorld.getBiomeKey(portalPos).orElse(null)) {
                                // Don't locate portals in biomes that won't generate portals in the first place
                                val nonNecterePos =
                                    HotMDimensions.getBaseCorrespondingNonNectereCoords(nectereWorld, portalPos)
                                if (nonNecterePos != null && checkExactBiomes(nonNectereWorld, nonNecterePos)) {
                                    val nonNectereStructurePos = HotMPortalOffsets.portal2StructurePos(nonNecterePos)

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
        chunkPos: ChunkPos,
        references: Int,
        seed: Long
    ) : StructureStart<DefaultFeatureConfig>(feature, chunkPos, references, seed) {
        override fun init(
            registryManager: DynamicRegistryManager,
            chunkGenerator: ChunkGenerator,
            structureManager: StructureManager,
            chunkPos: ChunkPos,
            biome: Biome,
            featureConfig: DefaultFeatureConfig,
            heightLimitView: HeightLimitView
        ) {
            val portalBiome = registryManager.get(Registry.BIOME_KEY).getKey(
                chunkGenerator.biomeSource.getBiomeForNoiseGen(
                    NecterePortalGen.getPortalX(chunkPos.x) shr 2,
                    64 shr 2,
                    NecterePortalGen.getPortalZ(chunkPos.z) shr 2
                )
            )

            // Make sure Nectere portal structures only spawn in Nectere biomes that are portalable.
            HotMBiomeData.ifPortalable(portalBiome) {
                children.add(
                    Piece(
                        random,
                        NecterePortalGen.getPortalStructureX(chunkPos.x),
                        NecterePortalGen.getPortalStructureZ(chunkPos.z)
                    )
                )
            }
            setBoundingBoxFromChildren()
        }
    }

    /**
     * Singular Nectere Portal Structure Piece.
     */
    class Piece : StructurePieceWithDimensions {
        constructor(random: Random, x: Int, z: Int) : super(
            HotMStructurePieces.NECTERE_PORTAL,
            x,
            64,
            z,
            5,
            4,
            5,
            getRandomHorizontalDirection(random)
        )

        constructor(world: ServerWorld, tag: NbtCompound) : super(HotMStructurePieces.NECTERE_PORTAL, tag)

        override fun generate(
            world: StructureWorldAccess,
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
            val portalPos =
                HotMPortalOffsets.transform2PortalPos(::applyXTransform, ::applyYTransform, ::applyZTransform)
            val otherWorld = HotMDimensions.getCorrespondingNonNectereWorld(serverWorld, portalPos) ?: return false
            val otherPoses = HotMDimensions.getBaseCorrespondingNonNectereCoords(world, portalPos)

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