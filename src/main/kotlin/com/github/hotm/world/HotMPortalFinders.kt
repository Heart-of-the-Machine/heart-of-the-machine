package com.github.hotm.world

import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.mixin.StructureFeatureAccessor
import com.github.hotm.util.BiomeUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.util.StreamUtils
import com.github.hotm.world.biome.HotMBiomeData
import com.github.hotm.world.biome.NectereBiomeData
import com.github.hotm.world.gen.feature.HotMStructureFeatures
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.RegistryWorldView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.StructureFeature
import java.util.stream.Stream

object HotMPortalFinders {
    /**
     * Used to indicate to the structure locator whether the given structure location is valid or it the locator should
     * keep searching.
     */
    class FindResult<T> private constructor(private val obj: Any?) {
        companion object {
            fun <T> done(obj: T): FindResult<T> = FindResult(obj)
            fun <T> keepSearching(): FindResult<T> = FindResult(KeepSearching)
        }

        private object KeepSearching

        val isKeepSearching = obj == KeepSearching

        @Suppress("UNCHECKED_CAST")
        fun get(): T {
            if (obj == KeepSearching) {
                throw IllegalStateException("Called get on non-done find result")
            } else {
                return obj as T
            }
        }
    }

    /**
     * Searches the world in concentric square-rings of x-by-x chunk structure regions. Each structure region contains
     * at most one structure start and the location of that structure start can be determined.
     */
    fun <T> locate(
        world: WorldView,
        structureAccessor: StructureAccessor,
        startChunk: ChunkPos,
        maxRadius: Int,
        seed: Long,
        structureConfig: StructureConfig,
        structure: StructureFeature<*>,
        found: (StructureStart<*>) -> FindResult<T>
    ): T? {
        val spacing = structureConfig.spacing
        var curRadius = 0
        val chunkRandom = ChunkRandom()

        while (curRadius <= maxRadius) {
            for (structX in -curRadius..curRadius) {
                val xBorder = structX == -curRadius || structX == curRadius
                for (structZ in -curRadius..curRadius) {
                    val zBorder = structZ == -curRadius || structZ == curRadius
                    if (xBorder || zBorder) {
                        val curChunkX = startChunk.x + spacing * structX
                        val curChunkZ = startChunk.z + spacing * structZ

                        val chunkPos: ChunkPos =
                            structure.getStartChunk(structureConfig, seed, chunkRandom, curChunkX, curChunkZ)

                        val chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS)
                        val structureStart =
                            structureAccessor.getStructureStart(
                                ChunkSectionPos.from(chunk.pos, 0),
                                structure,
                                chunk
                            )

                        if (structureStart != null && structureStart.hasChildren()) {
                            val res = found(structureStart)
                            if (!res.isKeepSearching) {
                                return res.get()
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
     * Locates a non-nectere side portal for the given nectere biome, making sure the portal is not in a location
     * corresponding to a denied non-nectere biome.
     */
    private fun locateNonNectereSidePortal(
        nectereWorld: RegistryWorldView,
        structureAccessor: StructureAccessor,
        blockPos: BlockPos,
        maxRadius: Int,
        skipExistingChunks: Boolean,
        seed: Long,
        structureConfig: StructureConfig,
        biomeKey: RegistryKey<Biome>,
        nonNectereWorld: ServerWorld
    ): BlockPos? {
        return locate(
            nectereWorld,
            structureAccessor,
            ChunkPos(blockPos),
            maxRadius,
            seed,
            structureConfig,
            HotMStructureFeatures.NECTERE_PORTAL
        ) { structureStart ->
            val portalPos = HotMPortalOffsets.structure2PortalPos(structureStart.blockPos)

            if (biomeKey == nectereWorld.getBiomeKey(portalPos).orElse(null)) {
                // Don't locate portals in biomes that won't generate portals in the first place

                val nonNecterePos = HotMBiomeData.ifData(nectereWorld.getBiomeKey(portalPos)) { biomeData ->
                    HotMLocationConversions.nectere2StartNon(portalPos, biomeData)
                }

                if (nonNecterePos != null && BiomeUtils.checkNonNectereBiomes(nonNectereWorld, nonNecterePos)) {
                    val nonNectereStructurePos = HotMPortalOffsets.portal2StructurePos(nonNecterePos)

                    if (skipExistingChunks && structureStart.isInExistingChunk) {
                        structureStart.incrementReferences()
                        return@locate FindResult.done(nonNectereStructurePos)
                    }

                    if (!skipExistingChunks) {
                        return@locate FindResult.done(nonNectereStructurePos)
                    }
                }
            }

            return@locate FindResult.keepSearching()
        }
    }


    /**
     * Locates a Nectere portal in a non-Nectere dimension.
     */
    fun locateNonNectereSidePortal(
        currentWorld: ServerWorld,
        currentPos: BlockPos,
        radius: Int,
        skipExistingChunks: Boolean
    ): BlockPos? {
        val nectereWorld = HotMDimensions.getNectereWorld(currentWorld.server)

        return HotMBiomeData.streamPortalables(currentWorld.registryKey).flatMap { nectereBiome ->
            val necterePos = HotMLocationConversions.non2StartNectere(currentPos, nectereBiome)!!

            val foundPos = locateNonNectereSidePortal(
                nectereWorld,
                nectereWorld.structureAccessor,
                necterePos,
                radius,
                skipExistingChunks,
                nectereWorld.seed,
                nectereWorld.chunkManager.chunkGenerator.structuresConfig.getForType(HotMStructureFeatures.NECTERE_PORTAL)
                    ?: error("Null Nectere Portal structure config"),
                nectereBiome.biome,
                currentWorld
            )

            StreamUtils.ofNullable(foundPos)
        }.min(Comparator.comparing { portalPos -> portalPos.getSquaredDistance(currentPos) })
            .orElse(null)
    }

    /**
     * Finds a Nectere portal like object in the world given a list of block positions and a filter predicate.
     */
    fun findNecterePortal(world: WorldView, newPoses: List<BlockPos>, predicate: (BlockPos) -> Boolean): BlockPos? {
        for (offset in 0 until world.height) {
            for (init in newPoses) {
                val up = init.up(offset)
                val down = init.down(offset)

                if (!world.isOutOfHeightLimit(up) && predicate(up)) {
                    return up
                }

                if (!world.isOutOfHeightLimit(down) && predicate(down)) {
                    return down
                }
            }
        }

        return null
    }

    /**
     * Finds a Nectere portal like object linked to this world and position.
     */
    fun findNecterePortal(world: ServerWorld, pos: BlockPos, predicate: (DimBlockPos) -> Boolean): DimBlockPos? {
        if (world.registryKey == HotMDimensions.NECTERE_KEY) {
            val otherWorld = HotMLocationConversions.nectere2NonWorld(world, pos)
            val otherPoses = HotMLocationConversions.nectere2AllNon(world, pos).toList()

            return if (otherWorld != null && otherPoses.isNotEmpty()) {
                findNecterePortal(otherWorld, otherPoses) {
                    predicate(DimBlockPos(otherWorld.registryKey, it))
                }?.let { DimBlockPos(otherWorld.registryKey, it) }
            } else {
                null
            }
        } else {
            val nectereWorld = HotMDimensions.getNectereWorld(world.server)
            val necterePoses = HotMLocationConversions.non2AllNectere(world, pos, nectereWorld).toList()

            return if (necterePoses.isNotEmpty()) {
                findNecterePortal(nectereWorld, necterePoses) {
                    predicate(DimBlockPos(HotMDimensions.NECTERE_KEY, it))
                }?.let { DimBlockPos(HotMDimensions.NECTERE_KEY, it) }
            } else {
                null
            }
        }
    }

    /**
     * Attempt to find a Nectere portal among a list of destination positions.
     */
    fun findNecterePortal(world: WorldView, newPoses: List<BlockPos>): BlockPos? {
        return findNecterePortal(
            world,
            newPoses
        ) { world.getBlockState(it).block == HotMBlocks.NECTERE_PORTAL }?.let { findPortalBase(world, it) }
    }

    /**
     * When given a portal block, finds the base portal block.
     */
    private fun findPortalBase(world: WorldView, portalPos: BlockPos): BlockPos {
        val mut = portalPos.mutableCopy()
        while (world.getBlockState(mut.down()).block == HotMBlocks.NECTERE_PORTAL) {
            mut.move(Direction.DOWN)
        }
        return mut.toImmutable()
    }

    /**
     * Gets the non-Nectere-coordinates of all Nectere portals within this chunk.
     */
    fun getNonNecterePortalCoords(
        registryManager: DynamicRegistryManager,
        currentWorldKey: RegistryKey<World>,
        currentPos: ChunkPos,
        nectereWorld: ServerWorld
    ): Stream<BlockPos> {
        if (currentWorldKey == HotMDimensions.NECTERE_KEY) {
            throw IllegalArgumentException("Cannot get non-Nectere portal gen coords in a Nectere world")
        }

        return HotMBiomeData.streamPortalables(currentWorldKey).flatMap { nectereBiome ->
            HotMLocationConversions.non2AllNectere(currentPos, nectereBiome).flatMap { necterePos ->
                getNonNecterePortalCoordsForBiome(
                    registryManager,
                    currentPos,
                    nectereWorld,
                    nectereBiome,
                    necterePos
                )
            }
        }
    }

    /**
     * Gets the non-Nectere-side location of all Nectere portals for the given Nectere-side chunk and Nectere-side biome.
     */
    private fun getNonNecterePortalCoordsForBiome(
        registryManager: DynamicRegistryManager,
        currentPos: ChunkPos,
        nectereWorld: ServerWorld,
        nectereBiomeData: NectereBiomeData,
        necterePos: ChunkPos
    ): Stream<BlockPos> {
        val chunkRandom = ChunkRandom()
        val structureConfig =
            nectereWorld.chunkManager.chunkGenerator.structuresConfig.getForType(HotMStructureFeatures.NECTERE_PORTAL)
        val portalChunk = HotMStructureFeatures.NECTERE_PORTAL.getStartChunk(
            structureConfig,
            nectereWorld.seed,
            chunkRandom,
            necterePos.x,
            necterePos.z
        )

        val chunkGenerator = nectereWorld.chunkManager.chunkGenerator
        val biomeSource = chunkGenerator.biomeSource
        val biomeRegistry = registryManager[Registry.BIOME_KEY]

        @Suppress("cast_never_succeeds")
        if ((HotMStructureFeatures.NECTERE_PORTAL as StructureFeatureAccessor).callShouldStartAt(
                chunkGenerator,
                biomeSource,
                nectereWorld.seed,
                chunkRandom,
                necterePos,
                biomeRegistry[nectereBiomeData.biome],
                portalChunk,
                FeatureConfig.DEFAULT,
                nectereWorld
            )
        ) {
            val necterePortalPos =
                BlockPos(
                    HotMPortalGenPositions.chunk2PortalX(portalChunk.x),
                    64,
                    HotMPortalGenPositions.chunk2PortalZ(portalChunk.z)
                )

            val biomeId = nectereWorld.getBiomeKey(necterePortalPos).orElse(null)

            return if (nectereBiomeData.biome == biomeId) {
                val portalPos = HotMLocationConversions.nectere2StartNon(necterePortalPos, nectereBiomeData)!!

                if (portalPos.x shr 4 == currentPos.x && portalPos.z shr 4 == currentPos.z) {
                    Stream.of(portalPos)
                } else {
                    Stream.empty()
                }
            } else {
                Stream.empty()
            }
        } else {
            return Stream.empty()
        }
    }
}