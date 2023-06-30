package com.github.hotm.mod.world

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.world.biome.NecterePortalData
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.WorldAccess

object HotMLocationConversions {

    /* Direct Conversions */

    /**
     * Converts a non-nectere side x or z coordinate into a nectere side x or z coordinate.
     */
    private fun non2Nectere(i: Int, nectereBiome: NecterePortalData): Int {
        return MathHelper.floor(i.toDouble() / nectereBiome.coordinateFactor)
    }

    /**
     * Converts a nectere side x or z coordinate into a non-nectere side x or z coordinate.
     */
    private fun nectere2Non(i: Int, nectereBiome: NecterePortalData): Int {
        return MathHelper.floor(i.toDouble() * nectereBiome.coordinateFactor)
    }

    /**
     * Converts a non-nectere side block pos into the first (most negative) nectere side block pos.
     */
    fun non2StartNectere(nonPos: BlockPos, nectereBiome: NecterePortalData): BlockPos {
        return BlockPos(non2Nectere(nonPos.x, nectereBiome), nonPos.y, non2Nectere(nonPos.z, nectereBiome))
    }

    /**
     * Converts a nectere side block pos into the first (most negative) non-nectere side block pos.
     */
    fun nectere2StartNon(necterePos: BlockPos, nectereBiome: NecterePortalData): BlockPos {
        return BlockPos(nectere2Non(necterePos.x, nectereBiome), necterePos.y, nectere2Non(necterePos.z, nectereBiome))
    }

    fun nectere2NonWorld(nectereWorld: ServerWorld, portalHolder: NecterePortalData.Holder): ServerWorld? {
        val world = nectereWorld.server.getWorld(portalHolder.data.targetWorld)
        if (world == null) {
            HotMLog.LOG.warn("Attempted to get non-existent world for Nectere biome (${portalHolder.biome}) with world key: ${portalHolder.data.targetWorld}")
        }
        return world
    }

    /**
     * Gets all Nectere-side block locations that could connect to the current non-Nectere-side block location.
     */
    fun non2AllNectereUnchecked(currentWorld: ServerWorld, currentPos: BlockPos): Sequence<BlockPos> {
        return NecterePortalData.BIOMES_BY_WORLD.get(currentWorld.registryKey).asSequence().map { nectereBiome ->
            non2StartNectere(currentPos, nectereBiome.data)
        }
    }

    /* Conversions That Read Chunks */

    fun nectere2NonWorldC(nectereWorld: ServerWorld, necterePos: BlockPos): ServerWorld? {
        val biomeKey = nectereWorld.getBiome(necterePos).key

        return NecterePortalData.ifData(biomeKey) { portalHolder ->
            nectere2NonWorld(nectereWorld, portalHolder)
        }
    }

    /**
     * Gets the non-Nectere-side coordinate of a *generated* Nectere portal.
     */
    fun nectere2StartNonC(nectereWorld: WorldAccess, necterePos: BlockPos): BlockPos? {
        val biomeKey = nectereWorld.getBiome(necterePos).key

        return NecterePortalData.ifData(biomeKey) { biomeHolder ->
            nectere2StartNon(necterePos, biomeHolder.data)
        }
    }
}
