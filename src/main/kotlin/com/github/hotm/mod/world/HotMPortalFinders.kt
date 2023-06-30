package com.github.hotm.mod.world

import com.github.hotm.mod.block.HotMPointOfInterestTypes
import com.github.hotm.mod.world.biome.NecterePortalData
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.streams.asSequence
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.poi.PointOfInterestStorage

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
                .filter { worldBorder.contains(it.pos) && destWorld.isTopSolid(it.pos.down(), entity) }
                .minByOrNull { it.pos.getSquaredDistance(nonPos) }

            if (poi == null) {
                // TODO: no portal found, we need to create it
                if (worldBorder.contains(nonPos)) {
                    return tempFindDepositSpace(destWorld, nonPos, entity) ?: nonPos
                } else return null
            }

            poi.pos
        }
    }

    fun findOrCreateNecterePortal(
        srcWorld: ServerWorld, portalPos: BlockPos, nectereWorld: ServerWorld, entity: Entity
    ): BlockPos? {
        TODO("Not yet implemented")
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
        return destWorld.getBlockState(testPos).isAir && destWorld.getBlockState(testPos.up()).isAir
            && destWorld.isTopSolid(testPos, entity)
    }
}
