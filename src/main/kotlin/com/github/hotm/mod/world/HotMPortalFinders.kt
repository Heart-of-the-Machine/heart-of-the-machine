package com.github.hotm.mod.world

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.block.HotMPointOfInterestTypes
import com.github.hotm.mod.world.biome.NecterePortalData
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.poi.PointOfInterestStorage
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.streams.asSequence
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World

object HotMPortalFinders {
    fun findOrCreateNonPortal(
        nectereWorld: ServerWorld, portalPos: BlockPos, destWorld: ServerWorld, entity: Entity
    ): BlockPos? {
        val biomeKey = nectereWorld.getBiome(portalPos).key

        return NecterePortalData.ifData(biomeKey) { portalHolder ->
            // try finding a portal
            val nonPos = HotMLocationConversions.nectere2StartNon(portalPos, portalHolder.data)
            val radius = ceil(portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

            // TODO: Pregen portals

            val worldBorder = destWorld.worldBorder

            val poi = destWorld.pointOfInterestStorage.getInSquare(
                { it.key.getOrNull() == HotMPointOfInterestTypes.NECTERE_PORTAL },
                nonPos, radius, PointOfInterestStorage.OccupationStatus.ANY
            ).asSequence()
                .filter { poi ->
                    worldBorder.contains(poi.pos)
                        && destWorld.getBlockState(poi.pos.down()).isOf(HotMBlocks.GLOWY_OBELISK_PART)
                }
                .minByOrNull { it.pos.getSquaredDistance(nonPos) }

            if (poi == null) {
                if (worldBorder.contains(nonPos)) {
                    val outPos = tempFindDepositSpace(destWorld, nonPos, entity) ?: nonPos
                    // TODO: no portal found, we need to create it
                    destWorld.setBlockState(outPos, HotMBlocks.NECTERE_PORTAL.defaultState)
                    destWorld.setBlockState(outPos.down(), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                    return outPos
                } else return null
            }

            poi.pos
        }
    }

    fun findOrCreateNecterePortal(
        srcWorld: ServerWorld, portalPos: BlockPos, nectereWorld: ServerWorld, entity: Entity
    ): BlockPos? {
        val worldborder = nectereWorld.worldBorder

        data class Found(val foundPos: BlockPos, val necterePos: BlockPos)

        val found =
            NecterePortalData.BIOMES_BY_WORLD.get(srcWorld.registryKey).asSequence().mapNotNull { portalHolder ->
                val necterePos = HotMLocationConversions.non2StartNectere(portalPos, portalHolder.data)
                val radius = ceil(1.0 / portalHolder.data.coordinateFactor).toInt().coerceAtLeast(1)

                // no portal pregenning needed on nectere side

                nectereWorld.pointOfInterestStorage.getInSquare(
                    { it.key.getOrNull() == HotMPointOfInterestTypes.NECTERE_PORTAL },
                    necterePos, radius, PointOfInterestStorage.OccupationStatus.ANY
                ).asSequence()
                    .filter { poi ->
                        worldborder.contains(poi.pos)
                            && nectereWorld.getBlockState(poi.pos.down()).isOf(HotMBlocks.GLOWY_OBELISK_PART)
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
                val outPos = tempFindDepositSpace(nectereWorld, necterePos, entity) ?: necterePos
                // TODO: no portal found, we need to create it
                nectereWorld.setBlockState(outPos, HotMBlocks.NECTERE_PORTAL.defaultState)
                nectereWorld.setBlockState(outPos.down(), HotMBlocks.GLOWY_OBELISK_PART.defaultState)
                return outPos
            }

            return null
        }
    }

    fun tempFindDepositSpace(destWorld: ServerWorld, destPos: BlockPos, entity: Entity): BlockPos? {
        val mutable = destPos.mutableCopy()
        var radius = 0
        while (radius < destWorld.height) {
            // try up
            mutable.setY(destPos.y + radius)
            if (tempIsValidDepositSpace(destWorld, mutable, entity)) return mutable.toImmutable()

            // try down
            mutable.setY(destPos.y - radius)
            if (tempIsValidDepositSpace(destWorld, mutable, entity)) return mutable.toImmutable()

            radius++
        }

        return null
    }

    fun tempIsValidDepositSpace(destWorld: ServerWorld, testPos: BlockPos, entity: Entity): Boolean {
        return !destWorld.getBlockState(testPos).shouldSuffocate(destWorld, testPos)
            && !destWorld.getBlockState(testPos.up()).shouldSuffocate(destWorld, testPos.up())
            && destWorld.isTopSolid(testPos.down(), entity)
    }

//    fun getNonNecterePortalCoords(
//        registryManager: DynamicRegistryManager, currentWorldKey: RegistryKey<World>, chunkPos: ChunkPos,
//        nectereWorld: ServerWorld
//    ): Sequence<BlockPos> {
//        if (currentWorldKey == HotMDimensions.NECTERE_KEY) throw IllegalArgumentException("Cannot get non-Nectere portal gen coords in a Nectere world")
//
//        return NecterePortalData.BIOMES_BY_WORLD.get(currentWorldKey).asSequence().mapNotNull { portalHolder ->
//
//        }
//    }
}
