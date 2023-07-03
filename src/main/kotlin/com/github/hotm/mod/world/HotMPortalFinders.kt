package com.github.hotm.mod.world

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.block.HotMPointOfInterestTypes
import com.github.hotm.mod.world.biome.NecterePortalData
import com.github.hotm.mod.world.gen.HotMPortalGen
import com.github.hotm.mod.world.gen.structure.HotMStructures
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.streams.asSequence
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.ServerTask
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.structure.ConcentricRingPlacementCalculator
import net.minecraft.structure.RandomSpreadStructurePlacement
import net.minecraft.structure.StructureManager
import net.minecraft.structure.StructureStart
import net.minecraft.util.math.*
import net.minecraft.world.RegistryWorldView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.feature.StructureFeature
import net.minecraft.world.poi.PointOfInterestStorage

object HotMPortalFinders {
    /**
     * Finds or creates a non-Nectere-side portal coming from the given position in the nectere.
     */
    fun findOrCreateNonNecterePortal(
        nectereWorld: ServerWorld, portalPos: BlockPos, destWorld: ServerWorld, callback: (BlockPos?) -> Unit
    ) {
        val biomeKey = nectereWorld.getBiome(portalPos).key.getOrNull()
        if (biomeKey == null) {
            callback(null)
            return
        }

        val portalHolder = NecterePortalData.BIOMES_BY_ID[biomeKey]
        if (portalHolder == null) {
            callback(null)
            return
        }

        // try finding a portal
        val nonPos = HotMLocationConversions.nectere2StartNon(portalPos, portalHolder.data)
        val radius = ceil(portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

        preloadChunks(destWorld, nonPos, radius)

        HotMPortalGen.pregenPortal(destWorld, ChunkPos(nonPos))

        val server = destWorld.server
        server.send(ServerTask(server.ticks) {
            val worldBorder = destWorld.worldBorder

            val poi = destWorld.pointOfInterestStorage.getInSquare(
                { it.key.getOrNull() == HotMPointOfInterestTypes.NECTERE_PORTAL },
                nonPos, radius, PointOfInterestStorage.OccupationStatus.ANY
            ).asSequence()
                .filter { poi ->
                    worldBorder.contains(poi.pos) && isValidPortal(destWorld, poi.pos)
                }
                .minByOrNull { it.pos.getSquaredDistance(nonPos) }

            if (poi == null) {
                if (worldBorder.contains(nonPos)) {
                    val outPos = tempFindDepositSpace(destWorld, nonPos) ?: nonPos
                    // TODO: no portal found, we need to create it
                    destWorld.setBlockState(outPos, HotMBlocks.NECTERE_PORTAL.defaultState)
                    destWorld.setBlockState(
                        outPos.up(),
                        HotMBlocks.NECTERE_PORTAL.defaultState.with(Properties.FACING, Direction.DOWN)
                    )
                    destWorld.setBlockState(outPos.down(), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                    destWorld.setBlockState(outPos.up(2), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                    callback(outPos)
                } else {
                    callback(null)
                }
            } else {
                callback(poi.pos)
            }
        })
    }

    /**
     * Finds a non-Nectere-side portal, given the Nectere-side biome it's coming from.
     */
    fun findNonNecterePortal(
        nonNectereWorld: ServerWorld, testPos: BlockPos, portalHolder: NecterePortalData.Holder
    ): BlockPos? {
        val radius = ceil(portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)
        val worldBorder = nonNectereWorld.worldBorder

        // preloading *probably* isn't needed here

        return nonNectereWorld.pointOfInterestStorage.getInSquare(
            { it.key.getOrNull() == HotMPointOfInterestTypes.NECTERE_PORTAL },
            testPos, radius, PointOfInterestStorage.OccupationStatus.ANY
        ).asSequence()
            .filter { worldBorder.contains(it.pos) && isValidPortal(nonNectereWorld, it.pos) }
            .minByOrNull { it.pos.getSquaredDistance(testPos) }
            ?.pos
    }

    /**
     * Finds or creates a Nectere-side portal coming from the given position not in the nectere.
     */
    fun findOrCreateNecterePortal(
        srcWorld: ServerWorld, portalPos: BlockPos, nectereWorld: ServerWorld, callback: (BlockPos?) -> Unit
    ) {
        for (portalHolder in NecterePortalData.BIOMES_BY_WORLD.get(srcWorld.registryKey)) {
            val necterePos = HotMLocationConversions.non2StartNectere(portalPos, portalHolder.data)
            val radius = ceil(1.0 / portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

            // make sure structures are generated
            preloadChunks(nectereWorld, necterePos, radius)
        }

        val server = srcWorld.server
        server.send(ServerTask(server.ticks) {
            val worldborder = nectereWorld.worldBorder

            data class Found(val foundPos: BlockPos, val necterePos: BlockPos)

            val found =
                NecterePortalData.BIOMES_BY_WORLD.get(srcWorld.registryKey).asSequence().mapNotNull { portalHolder ->
                    val necterePos = HotMLocationConversions.non2StartNectere(portalPos, portalHolder.data)
                    val radius = ceil(1.0 / portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

                    nectereWorld.pointOfInterestStorage.getInSquare(
                        { it.key.getOrNull() == HotMPointOfInterestTypes.NECTERE_PORTAL },
                        necterePos, radius, PointOfInterestStorage.OccupationStatus.ANY
                    ).asSequence()
                        .filter { poi ->
                            worldborder.contains(poi.pos)
                                && isValidPortal(nectereWorld, poi.pos)
                                && (nectereWorld.getBiome(poi.pos).key.getOrNull() == portalHolder.biome)
                        }
                        .minByOrNull { it.pos.getSquaredDistance(necterePos) }?.let { Found(it.pos, necterePos) }
                }
                    .minByOrNull { it.foundPos.getSquaredDistance(it.necterePos) }

            if (found != null) {
                callback(found.foundPos)
            } else {
                val necterePoses = NecterePortalData.BIOMES_BY_WORLD.get(srcWorld.registryKey).asSequence()
                    .mapNotNull {
                        val necterePos = HotMLocationConversions.non2StartNectere(portalPos, it.data)

                        if (nectereWorld.getBiome(necterePos).key.getOrNull() != it.biome) return@mapNotNull null

                        necterePos
                    }
                    .filter { worldborder.contains(it) }.toList()

                val necterePos = necterePoses.randomOrNull()

                if (necterePos != null) {
                    val outPos = tempFindDepositSpace(nectereWorld, necterePos) ?: necterePos
                    // TODO: no portal found, we need to create it
                    nectereWorld.setBlockState(outPos, HotMBlocks.NECTERE_PORTAL.defaultState)
                    nectereWorld.setBlockState(
                        outPos.up(),
                        HotMBlocks.NECTERE_PORTAL.defaultState.with(Properties.FACING, Direction.DOWN)
                    )
                    nectereWorld.setBlockState(outPos.down(), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                    nectereWorld.setBlockState(outPos.up(2), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                    callback(outPos)
                } else {
                    callback(null)
                }
            }
        })
    }

    private fun preloadChunks(world: ServerWorld, pos: BlockPos, radius: Int) {
        // FIXME: doesn't work
        for (chunkPos in ChunkPos.stream(ChunkPos(pos), MathHelper.floorDiv(radius, 16))) {
            world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL)
        }

        world.pointOfInterestStorage.preloadChunks(world, pos, radius)
    }

    /**
     * Checks if the portal found at the given position is valid.
     */
    private fun isValidPortal(world: ServerWorld, portalPos: BlockPos): Boolean {
        return world.getBlockState(portalPos.down()).isOf(HotMBlocks.GLOWY_OBELISK_PART)
    }

    /**
     * Finds a place to put a portal if no existing portals were found.
     */
    private fun tempFindDepositSpace(destWorld: ServerWorld, destPos: BlockPos): BlockPos? {
        val mutable = destPos.mutableCopy()
        var radius = 0
        while (radius < destWorld.height) {
            // try up
            mutable.setY(destPos.y + radius)
            if (tempIsValidDepositSpace(destWorld, mutable)) return mutable.toImmutable()

            // try down
            mutable.setY(destPos.y - radius)
            if (tempIsValidDepositSpace(destWorld, mutable)) return mutable.toImmutable()

            radius++
        }

        return null
    }

    /**
     * Checks if the given block position is a valid place to put a portal.
     */
    private fun tempIsValidDepositSpace(destWorld: ServerWorld, testPos: BlockPos): Boolean {
        return destWorld.getBlockState(testPos).isAir && destWorld.getBlockState(testPos.up()).isAir
            && !destWorld.getBlockState(testPos.down()).isAir
    }

    /**
     * The result of looking for a portal placement.
     */
    data class PortalPlacementResult(val portalXZ: BlockPos, val portalHolder: NecterePortalData.Holder)

    /**
     * Gets all the places in a non-nectere chunk that portals should be placed at (for non-nectere portal generation).
     */
    fun getNonNecterePortalPlacementsForChunk(
        currentWorldKey: RegistryKey<World>, currentPos: ChunkPos,
        nectereWorld: ServerWorld
    ): Sequence<PortalPlacementResult> {
        if (currentWorldKey == HotMDimensions.NECTERE_KEY) throw IllegalArgumentException("Cannot get non-Nectere portal gen coords in a Nectere world")

        val structure =
            nectereWorld.registryManager.get(RegistryKeys.STRUCTURE_FEATURE).get(HotMStructures.NECTERE_PORTAL)
        if (structure == null) {
            HotMLog.LOG.error("No 'hotm:nectere_portal' structure registered")
            return emptySequence()
        }

        val calculator = nectereWorld.chunkManager.method_46642()
        val placements = calculator.getFeaturePlacements(Holder.createDirect(structure))
        val placement = placements.asSequence().filterIsInstance<RandomSpreadStructurePlacement>().firstOrNull()
        if (placement == null) {
            HotMLog.LOG.error("No random spread structure placements for 'hotm:nectere_portal' registered")
            return emptySequence()
        }

        if (placements.size > 1) {
            HotMLog.LOG.warn("Multiple structure placements for 'hotm:nectere_portal' found, ignoring all but first.")
        }

        return NecterePortalData.BIOMES_BY_WORLD.get(currentWorldKey).asSequence().flatMap { portalHolder ->
            HotMLocationConversions.non2AllNectere(currentPos, portalHolder.data).mapNotNull { necterePos ->
                getNonNecterePortalPlacementForBiome(
                    placement,
                    calculator,
                    currentPos,
                    nectereWorld,
                    portalHolder,
                    necterePos
                )?.let { PortalPlacementResult(it, portalHolder) }
            }
        }
    }

    /**
     * Gets the non-nectere portal position for the given nectere chunk and nectere biome.
     */
    private fun getNonNecterePortalPlacementForBiome(
        placement: RandomSpreadStructurePlacement,
        calculator: ConcentricRingPlacementCalculator,
        currentPos: ChunkPos,
        nectereWorld: ServerWorld,
        portalHolder: NecterePortalData.Holder,
        necterePos: ChunkPos
    ): BlockPos? {
        val startChunk = placement.getPotentialStartChunk(calculator.worldSeed, necterePos.x, necterePos.z)

        val necterePortalPos = BlockPos(
            HotMPortalGenPositions.chunk2PortalX(startChunk.x),
            64,
            HotMPortalGenPositions.chunk2PortalZ(startChunk.z)
        )

        val biomeId = nectereWorld.getBiome(necterePortalPos).key.getOrNull()
        if (biomeId != portalHolder.biome) return null

        val portalPos = HotMLocationConversions.nectere2StartNon(necterePortalPos, portalHolder.data)

        return if (portalPos.x shr 4 == currentPos.x && portalPos.z shr 4 == currentPos.z) {
            portalPos
        } else null
    }

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
        structureManager: StructureManager,
        startChunk: ChunkPos,
        maxRadius: Int,
        seed: Long,
        structure: StructureFeature,
        placement: RandomSpreadStructurePlacement,
        found: (StructureStart) -> FindResult<T>
    ): T? {
        val spacing = placement.spacing
        var curRadius = 0

        while (curRadius <= maxRadius) {
            for (structX in -curRadius..curRadius) {
                val xBorder = structX == -curRadius || structX == curRadius
                for (structZ in -curRadius..curRadius) {
                    val zBorder = structZ == -curRadius || structZ == curRadius
                    if (xBorder || zBorder) {
                        val curChunkX = startChunk.x + spacing * structX
                        val curChunkZ = startChunk.z + spacing * structZ

                        val chunkPos = placement.getPotentialStartChunk(seed, curChunkX, curChunkZ)

                        val chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS)
                        val structureStart =
                            structureManager.getStructureStart(
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
    private fun locateNonNectereSidePortalStructure(
        nectereWorld: RegistryWorldView,
        structureAccessor: StructureManager,
        blockPos: BlockPos,
        maxRadius: Int,
        seed: Long,
        biomeKey: RegistryKey<Biome>,
        structure: StructureFeature,
        placement: RandomSpreadStructurePlacement
    ): BlockPos? {
        return locate(
            nectereWorld,
            structureAccessor,
            ChunkPos(blockPos),
            maxRadius,
            seed,
            structure,
            placement
        ) { structureStart ->
            val portalPos = HotMPortalGenPositions.chunk2PortalXZ(structureStart.pos)

            val foundBiome = nectereWorld.getBiome(portalPos).key

            if (biomeKey == foundBiome.getOrNull()) {
                // Don't locate portals in biomes that won't generate portals in the first place

                val nonNecterePos = NecterePortalData.ifData(foundBiome) { portalHolder ->
                    HotMLocationConversions.nectere2StartNon(portalPos, portalHolder.data)
                }

                if (nonNecterePos != null) {
                    val nonNectereStructurePos = HotMPortalOffsets.portal2StructurePos(nonNecterePos)

                    return@locate FindResult.done(nonNectereStructurePos)
                }
            }

            return@locate FindResult.keepSearching()
        }
    }

    /**
     * Locates a Nectere portal in a non-Nectere dimension.
     */
    fun locateNonNectereSidePortalStructure(
        currentWorld: ServerWorld,
        currentPos: BlockPos,
        radius: Int
    ): BlockPos? {
        val nectereWorld = HotMDimensions.getNectere(currentWorld.server)
        val structureRegistry = nectereWorld.registryManager.get(RegistryKeys.STRUCTURE_FEATURE)

        val structureHolder = structureRegistry.getHolder(HotMStructures.NECTERE_PORTAL).getOrNull()
        if (structureHolder == null) {
            HotMLog.LOG.error("No 'hotm:nectere_portal' structure registered")
            return null
        }

        val calculator = nectereWorld.chunkManager.method_46642()
        val placements = calculator.getFeaturePlacements(structureHolder)
        val placement = placements.asSequence().filterIsInstance<RandomSpreadStructurePlacement>().firstOrNull()
        if (placement == null) {
            HotMLog.LOG.error("No random spread structure placements for 'hotm:nectere_portal' registered")
            return null
        }

        if (placements.size > 1) {
            HotMLog.LOG.warn("Multiple structure placements for 'hotm:nectere_portal' found, ignoring all but first.")
        }

        return NecterePortalData.BIOMES_BY_WORLD.get(currentWorld.registryKey).asSequence().mapNotNull { portalHolder ->
            val necterePos = HotMLocationConversions.non2StartNectere(currentPos, portalHolder.data)

            locateNonNectereSidePortalStructure(
                nectereWorld,
                nectereWorld.structureManager,
                necterePos,
                radius,
                nectereWorld.seed,
                portalHolder.biome,
                structureHolder.value(),
                placement
            )
        }.minByOrNull { portalPos -> portalPos.getSquaredDistance(currentPos) }
    }
}
