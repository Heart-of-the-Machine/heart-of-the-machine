package com.github.hotm.world.gen.feature

import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMPortalGenPositions
import com.github.hotm.world.biome.HotMBiomeData
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructurePieceWithDimensions
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.world.HeightLimitView
import net.minecraft.world.StructureWorldAccess
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
class NecterePortalStructureFeature(config: Codec<DefaultFeatureConfig>) :
    StructureFeature<DefaultFeatureConfig>(config) {

    override fun getStructureStartFactory(): StructureStartFactory<DefaultFeatureConfig> {
        return StructureStartFactory { feature, pos, references, seed ->
            Start(feature, pos, references, seed)
        }
    }

    /**
     * Nectere Portal Structure Start.
     */
    class Start(
        feature: StructureFeature<DefaultFeatureConfig>,
        chunkPos: ChunkPos,
        references: Int,
        private val seed: Long
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
                HotMDimensions.NECTERE_TYPE.biomeAccessType.getBiome(
                    seed,
                    HotMPortalGenPositions.chunk2PortalX(chunkPos.x),
                    0,
                    HotMPortalGenPositions.chunk2PortalZ(chunkPos.z),
                    chunkGenerator.biomeSource
                )
            )

            // Make sure Nectere portal structures only spawn in Nectere biomes that are portalable.
            HotMBiomeData.ifPortalable(portalBiome) {
                children.add(
                    Piece(
                        random,
                        HotMPortalGenPositions.chunk2StructureX(chunkPos.x),
                        HotMPortalGenPositions.chunk2StructureZ(chunkPos.z)
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
            val spawnerPos = HotMPortalGenPositions.getPortalSpawnerPos(chunkPos)

            // Instead of checking validity and generating the portal structure here, we'll do it in a BlockEntity on
            // the main server thread where we don't have to worry about deadlocks when we access other dimensions.
            if (boundingBox.contains(spawnerPos)) {
                val originalBlock = world.getBlockState(spawnerPos)

                world.setBlockState(spawnerPos, HotMBlocks.NECTERE_PORTAL_SPAWNER.defaultState, 3)
                (world.getBlockEntity(spawnerPos) as? NecterePortalSpawnerBlockEntity)?.let { be ->
                    be.originalBlock = originalBlock
                    be.structureCtx =
                        NecterePortalSpawnerBlockEntity.StructureContext(getBoundingBox(), facing, mirror, rotation)
                }
            }

            if (!method_14839(world, boundingBox, 0)) {
                return false
            }

            return true
        }
    }
}