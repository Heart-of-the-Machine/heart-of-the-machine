package com.github.hotm.world

import com.github.hotm.misc.HotMLog
import com.github.hotm.world.biome.HotMBiomeData
import com.github.hotm.world.biome.NectereBiomeData
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.WorldAccess
import java.util.stream.IntStream
import java.util.stream.Stream

/**
 * Utilities for converting from one dimension's coordinate system to another.
 */
object HotMLocationConversions {

    /* Direct Conversions */

    /**
     * Converts a non-nectere side x or z coordinate into a nectere side x or z coordinate.
     */
    private fun non2Nectere(i: Int, nectereBiome: NectereBiomeData): Int {
        return MathHelper.floor(i.toDouble() / nectereBiome.coordinateMultiplier)
    }

    /**
     * Converts a nectere side x or z coordinate into a non-nectere side x or z coordinate.
     */
    private fun nectere2Non(i: Int, nectereBiome: NectereBiomeData): Int {
        return MathHelper.floor(i.toDouble() * nectereBiome.coordinateMultiplier)
    }

    /**
     * Converts a non-nectere side block pos into a stream of nectere side block poses for a given biome.
     */
    fun non2AllNectere(nonPos: BlockPos, nectereBiome: NectereBiomeData): Stream<BlockPos> {
        return if (nectereBiome.isPortalable) {
            if (nectereBiome.coordinateMultiplier < 1.0) {
                // If the non-nectere block corresponds to multiple nectere blocks, then just give all of them
                IntStream.range(non2Nectere(nonPos.x, nectereBiome), non2Nectere(nonPos.x + 1, nectereBiome))
                    .mapToObj { it }.flatMap { x ->
                        IntStream.range(non2Nectere(nonPos.z, nectereBiome), non2Nectere(nonPos.z + 1, nectereBiome))
                            .mapToObj { z -> BlockPos(x, nonPos.y, z) }
                    }
            } else {
                Stream.of(BlockPos(non2Nectere(nonPos.x, nectereBiome), nonPos.y, non2Nectere(nonPos.z, nectereBiome)))
            }
        } else {
            Stream.empty()
        }
    }

    /**
     * Converts a nectere side block pos into a stream of non-nectere side block poses for a given biome.
     */
    fun nectere2AllNon(necterePos: BlockPos, nectereBiome: NectereBiomeData): Stream<BlockPos> {
        return if (nectereBiome.isPortalable) {
            if (nectereBiome.coordinateMultiplier > 1.0) {
                IntStream.range(nectere2Non(necterePos.x, nectereBiome), nectere2Non(necterePos.x + 1, nectereBiome))
                    .mapToObj { it }.flatMap { x ->
                        IntStream.range(
                            nectere2Non(necterePos.z, nectereBiome),
                            nectere2Non(necterePos.z + 1, nectereBiome)
                        ).mapToObj { z -> BlockPos(x, necterePos.y, z) }
                    }
            } else {
                Stream.of(
                    BlockPos(
                        nectere2Non(necterePos.x, nectereBiome),
                        necterePos.y,
                        nectere2Non(necterePos.z, nectereBiome)
                    )
                )
            }
        } else {
            Stream.empty()
        }
    }

    /**
     * Converts a non-nectere side chunk pos into a stream of nectere side chunk poses for a given biome.
     */
    fun non2AllNectere(nonPos: ChunkPos, nectereBiome: NectereBiomeData): Stream<ChunkPos> {
        return if (nectereBiome.isPortalable) {
            if (nectereBiome.coordinateMultiplier < 1.0) {
                IntStream.range(non2Nectere(nonPos.x, nectereBiome), non2Nectere(nonPos.x + 1, nectereBiome))
                    .mapToObj { it }.flatMap { x ->
                        IntStream.range(non2Nectere(nonPos.z, nectereBiome), non2Nectere(nonPos.z + 1, nectereBiome))
                            .mapToObj { z -> ChunkPos(x, z) }
                    }
            } else {
                Stream.of(ChunkPos(non2Nectere(nonPos.x, nectereBiome), non2Nectere(nonPos.z, nectereBiome)))
            }
        } else {
            Stream.empty()
        }
    }

    /**
     * Converts a nectere side chunk pos into a stream of non-nectere side chunk poses for a given biome.
     */
    fun nectere2AllNon(necterePos: ChunkPos, nectereBiome: NectereBiomeData): Stream<ChunkPos> {
        return if (nectereBiome.isPortalable) {
            if (nectereBiome.coordinateMultiplier > 1.0) {
                IntStream.range(nectere2Non(necterePos.x, nectereBiome), nectere2Non(necterePos.x + 1, nectereBiome))
                    .mapToObj { it }.flatMap { x ->
                        IntStream.range(
                            nectere2Non(necterePos.z, nectereBiome),
                            nectere2Non(necterePos.z + 1, nectereBiome)
                        ).mapToObj { z -> ChunkPos(x, z) }
                    }
            } else {
                Stream.of(ChunkPos(nectere2Non(necterePos.x, nectereBiome), nectere2Non(necterePos.z, nectereBiome)))
            }
        } else {
            Stream.empty()
        }
    }

    /**
     * Converts a non-nectere side block pos into the first (most negative) nectere side block pos.
     */
    fun non2StartNectere(nonPos: BlockPos, nectereBiome: NectereBiomeData): BlockPos? {
        return if (nectereBiome.isPortalable) {
            BlockPos(non2Nectere(nonPos.x, nectereBiome), nonPos.y, non2Nectere(nonPos.z, nectereBiome))
        } else {
            null
        }
    }

    /**
     * Converts a nectere side block pos into the first (most negative) non-nectere side block pos.
     */
    fun nectere2StartNon(necterePos: BlockPos, nectereBiome: NectereBiomeData): BlockPos? {
        return if (nectereBiome.isPortalable) {
            BlockPos(nectere2Non(necterePos.x, nectereBiome), necterePos.y, nectere2Non(necterePos.z, nectereBiome))
        } else {
            null
        }
    }

    /**
     * Converts a non-nectere side block pos into the first (most negative) nectere side block pos.
     */
    fun non2StartNectere(nonPos: ChunkPos, nectereBiome: NectereBiomeData): ChunkPos? {
        return if (nectereBiome.isPortalable) {
            ChunkPos(non2Nectere(nonPos.x, nectereBiome), non2Nectere(nonPos.z, nectereBiome))
        } else {
            null
        }
    }

    /**
     * Converts a nectere side block pos into the first (most negative) non-nectere side block pos.
     */
    fun nectere2StartNon(necterePos: ChunkPos, nectereBiome: NectereBiomeData): ChunkPos? {
        return if (nectereBiome.isPortalable) {
            ChunkPos(nectere2Non(necterePos.x, nectereBiome), nectere2Non(necterePos.z, nectereBiome))
        } else {
            null
        }
    }

    /* Complex Conversions */

    /**
     * Gets the non-Nectere-side world that connects to the current Nectere-side block location.
     */
    fun nectere2NonWorld(nectereWorld: ServerWorld, necterePos: BlockPos): ServerWorld? {
        val server = nectereWorld.server
        val biomeKey = nectereWorld.getBiomeKey(necterePos)

        return HotMBiomeData.ifPortalable(biomeKey) { biomeData ->
            val world = server.getWorld(biomeData.targetWorld)
            if (world == null) {
                HotMLog.log.warn("Attempted to get non-existent world for Nectere biome with world key: ${biomeData.targetWorld}")
            }
            world
        }
    }

    /**
     * Gets the non-Nectere-side block location that connects to the current Nectere-side block location.
     */
    fun nectere2AllNon(nectereWorld: WorldAccess, necterePos: BlockPos): Stream<BlockPos> {
        val biomeKey = nectereWorld.getBiomeKey(necterePos)

        return HotMBiomeData.ifData(biomeKey) { biomeData ->
            nectere2AllNon(necterePos, biomeData)
        } ?: Stream.empty()
    }

    /**
     * Gets all Nectere-side block locations that could connect to the current non-Nectere-side block location.
     */
    fun non2AllNectere(currentWorld: ServerWorld, currentPos: BlockPos, nectereWorld: ServerWorld): Stream<BlockPos> {
        return HotMBiomeData.streamPortalables(currentWorld.registryKey).flatMap { nectereBiome ->
            non2AllNectere(currentPos, nectereBiome).filter {
                nectereBiome.biome == nectereWorld.getBiomeKey(it).orElse(null)
            }
        }
    }

    /**
     * Gets the non-Nectere-side coordinate of a *generated* Nectere portal.
     */
    fun nectere2StartNon(nectereWorld: WorldAccess, necterePos: BlockPos): BlockPos? {
        val biomeKey = nectereWorld.getBiomeKey(necterePos)

        return HotMBiomeData.ifData(biomeKey) { biomeData ->
            nectere2StartNon(necterePos, biomeData)
        }
    }
}