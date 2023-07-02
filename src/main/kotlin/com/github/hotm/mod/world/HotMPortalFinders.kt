package com.github.hotm.mod.world

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.block.HotMPointOfInterestTypes
import com.github.hotm.mod.world.biome.NecterePortalData
import com.github.hotm.mod.world.gen.HotMPortalGen
import com.github.hotm.mod.world.gen.structure.HotMStructures
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.structure.ConcentricRingPlacementCalculator
import net.minecraft.structure.RandomSpreadStructurePlacement
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.poi.PointOfInterestStorage
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.streams.asSequence

object HotMPortalFinders {
    /**
     * Finds or creates a non-Nectere-side portal coming from the given position in the nectere.
     */
    fun findOrCreateNonNecterePortal(
        nectereWorld: ServerWorld, portalPos: BlockPos, destWorld: ServerWorld
    ): BlockPos? {
        val biomeKey = nectereWorld.getBiome(portalPos).key

        return NecterePortalData.ifData(biomeKey) { portalHolder ->
            // try finding a portal
            val nonPos = HotMLocationConversions.nectere2StartNon(portalPos, portalHolder.data)
            val radius = ceil(portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

            preloadChunks(destWorld, nonPos, radius)

            HotMPortalGen.pregenPortal(destWorld, ChunkPos(nonPos))

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
                    return outPos
                } else return null
            }

            poi.pos
        }
    }

    /**
     * Finds a non-Nectere-side portal, given the Nectere-side biome it's coming from.
     */
    fun findNonNecterePortal(
        nonNectereWorld: ServerWorld, testPos: BlockPos, portalHolder: NecterePortalData.Holder
    ): BlockPos? {
        val radius = ceil(portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)
        val worldBorder = nonNectereWorld.worldBorder

        preloadChunks(nonNectereWorld, testPos, radius)

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
    fun findOrCreateNecterePortal(srcWorld: ServerWorld, portalPos: BlockPos, nectereWorld: ServerWorld): BlockPos? {
        val worldborder = nectereWorld.worldBorder

        data class Found(val foundPos: BlockPos, val necterePos: BlockPos)

        val found =
            NecterePortalData.BIOMES_BY_WORLD.get(srcWorld.registryKey).asSequence().mapNotNull { portalHolder ->
                val necterePos = HotMLocationConversions.non2StartNectere(portalPos, portalHolder.data)
                val radius = ceil(1.0 / portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

                // make sure structures are generated
                preloadChunks(nectereWorld, necterePos, radius)

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
            return found.foundPos
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
                return outPos
            }

            return null
        }
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
        if (startChunk != necterePos) return null

        val necterePortalPos = BlockPos(
            HotMPortalGenPositions.chunk2PortalX(startChunk.x),
            64,
            HotMPortalGenPositions.chunk2PortalZ(startChunk.z)
        )

        val biomeId = nectereWorld.getBiome(necterePortalPos).key.getOrNull()
        if (biomeId != portalHolder.biome) return null

        val portalPos = HotMLocationConversions.nectere2StartNon(necterePortalPos, portalHolder.data)

        return if (HotMPortalGenPositions.portal2ChunkX(portalPos.x) == currentPos.x
            && HotMPortalGenPositions.portal2ChunkZ(portalPos.z) == currentPos.z
        ) {
            portalPos
        } else null
    }

    /**
     * The result of looking for a portal placement.
     */
    data class PortalPlacementResult(val portalXZ: BlockPos, val portalHolder: NecterePortalData.Holder)
}
