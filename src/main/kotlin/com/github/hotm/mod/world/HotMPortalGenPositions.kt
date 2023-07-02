package com.github.hotm.mod.world

import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.Heightmap
import net.minecraft.world.RegistryWorldView
import net.minecraft.world.StructureWorldAccess
import java.util.Random
import net.minecraft.world.HeightLimitView

object HotMPortalGenPositions {
    private const val WORLD_SEED_OFFSET = 0xDEADBEEFL
    private const val MIN_ROOF_HEIGHT = 32

    /**
     * Gets a consistent random for use in portal placement.
     */
    private fun getPortalPlacementRandom(world: StructureWorldAccess, portalX: Int, portalZ: Int): Random =
        Random(world.seed + (portalX * 31 + portalZ) * 31 + WORLD_SEED_OFFSET)

    /**
     * Gets the location of the portal spawner block entity within a chunk.
     */
    fun getPortalSpawnerPos(world: HeightLimitView, pos: ChunkPos): BlockPos = BlockPos(pos.startX, world.bottomY + 1, pos.startZ)

    /**
     * Scans the world at the given x and z coordinates for valid biomes and surfaces.
     */
    private fun findPortalPoses(world: RegistryWorldView, portalX: Int, portalZ: Int): FindContext {
        val surfaces = mutableListOf<Int>()
        val pos = BlockPos.Mutable(portalX, 0, portalZ)
        var prevAir = world.isAir(pos)
        var roof = -1

        for (y in world.bottomY..world.topY) {
            pos.y = y

            if (world.getBlockState(pos).block == Blocks.BEDROCK && y > MIN_ROOF_HEIGHT) {
                roof = y
                break
            }

            val air = world.isAir(pos)
            if (!prevAir && air) {
                surfaces.add(y)
            }
            prevAir = air
        }

        surfaces.removeAll((roof - 4)..roof)

        return FindContext(roof, surfaces)
    }

    /**
     * Finds a portal position with the given x and z coordinates.
     *
     * This does not do any biome checking for world-gen.
     */
    fun findPortalPos(world: StructureWorldAccess, portalX: Int, portalZ: Int): BlockPos {
        val random = getPortalPlacementRandom(world, portalX, portalZ)
        val find = findPortalPoses(world, portalX, portalZ)

        val surfaces = find.surfaces
        val roof = find.roof

        val structureY = when {
            surfaces.isEmpty() -> random.nextInt(
                if (roof > MIN_ROOF_HEIGHT) {
                    roof - 8
                } else {
                    124
                }
            ) + 4
            roof > MIN_ROOF_HEIGHT -> surfaces[random.nextInt(surfaces.size)]
            else -> world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, portalX, portalZ)
        }

        return BlockPos(portalX, HotMPortalOffsets.structure2PortalY(structureY), portalZ)
    }

    /**
     * Gets the x position of a portal structure feature from the portal's chunk x.
     */
    fun chunk2StructureX(chunkX: Int): Int {
        return chunkX.shl(4)
    }

    /**
     * Gets the x position of the portal in a portal structure feature from the portal's chunk x.
     */
    fun chunk2PortalX(chunkX: Int): Int {
        return HotMPortalOffsets.structure2PortalX(chunk2StructureX(chunkX))
    }

    /**
     * Gets the z position of a portal structure feature from the portal's chunk z.
     */
    fun chunk2StructureZ(chunkZ: Int): Int {
        return chunkZ.shl(4)
    }

    /**
     * Gets the z position of the portal in a portal structure feature from the portal's chunk z.
     */
    fun chunk2PortalZ(chunkZ: Int): Int {
        return HotMPortalOffsets.structure2PortalZ(chunk2StructureZ(chunkZ))
    }

    private data class FindContext(val roof: Int, val surfaces: List<Int>)
}
