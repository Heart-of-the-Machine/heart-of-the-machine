package com.github.hotm.world

import com.github.hotm.util.BiomeUtils
import com.github.hotm.util.StreamUtils
import com.github.hotm.world.biome.HotMBiomeData
import com.github.hotm.world.gen.feature.HotMStructureFeatures
import com.github.hotm.world.gen.feature.NecterePortalGen
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.RegistryWorldView
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.feature.StructureFeature

object HotMLocators {
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
            val portalPos = NecterePortalGen.portalPos(structureStart.pos)

            if (biomeKey == nectereWorld.getBiomeKey(portalPos).orElse(null)) {
                // Don't locate portals in biomes that won't generate portals in the first place

                val nonNecterePos = HotMBiomeData.ifData(nectereWorld.getBiomeKey(portalPos)) { biomeData ->
                    HotMLocationConversions.nectere2StartNon(portalPos, biomeData)
                }

                if (nonNecterePos != null && BiomeUtils.checkNonNectereBiome(nonNectereWorld, nonNecterePos)) {
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
}