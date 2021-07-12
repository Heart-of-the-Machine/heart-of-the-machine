package com.github.hotm.world

import com.github.hotm.config.HotMConfig
import com.github.hotm.mixin.EntityAccessor
import com.github.hotm.mixinapi.DimensionAdditions
import com.github.hotm.world.gen.HotMPortalGen
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import java.util.stream.Collectors

object HotMTeleporters {

    /**
     * Registers the nectere teleport handlers.
     */
    fun register() {
        DimensionAdditions.registerDefaultPlacer(
            HotMDimensions.NECTERE_KEY
        ) { oldEntity, destination -> getGenericTeleportTarget(oldEntity, destination) }
    }

    /**
     * Performs a teleportation between the Overworld and the Nectere.
     *
     * @return whether the portal that attempted to teleport the entity is in a valid location.
     */
    fun attemptNectereTeleportation(entity: Entity, world: World, portalPos: BlockPos): Boolean {
        return (world as? ServerWorld)?.let { serverWorld ->
            if ((entity as EntityAccessor).netherPortalCooldown > 0) {
                entity.netherPortalCooldown = entity.defaultNetherPortalCooldown

                true
            } else {
                val server = serverWorld.server

                if (world.registryKey == HotMDimensions.NECTERE_KEY) {
                    val newWorld = HotMLocationConversions.nectere2NonWorld(serverWorld, portalPos)
                    val newPoses =
                        HotMLocationConversions.nectere2AllNon(serverWorld, portalPos).collect(Collectors.toList())

                    // Don't bother checking non-nectere biome validity because that only matters for world-gen.
                    attemptTeleport(newWorld, newPoses, entity)
                } else {
                    val nectereWorld = HotMDimensions.getNectereWorld(server)
                    val newPoses = HotMLocationConversions.non2AllNectere(serverWorld, portalPos, nectereWorld)
                        .collect(Collectors.toList())

                    attemptTeleport(nectereWorld, newPoses, entity)
                }
            }
        } ?: true
    }

    /**
     * Attempts to perform a teleportation of the target entity to the destination world with a list of destination
     * positions.
     */
    private fun attemptTeleport(
        destWorld: ServerWorld?,
        destPoses: List<BlockPos>,
        entity: Entity
    ): Boolean {
        return if (destWorld != null && destPoses.isNotEmpty()) {
            HotMPortalGen.pregenPortals(destWorld, destPoses)

            var destPos = HotMPortalFinders.findNecterePortal(destWorld, destPoses)

            if (destPos == null && HotMConfig.CONFIG.generateMissingPortals) {
                destPos = HotMPortalGen.createNecterePortal(destWorld, destPoses)
            }

            val finalPos = destPos

            if (finalPos != null) {
                val res = DimensionAdditions.teleport(
                    entity,
                    destWorld
                ) { oldEntity, _ ->
                    getTeleportTarget(
                        oldEntity,
                        finalPos
                    )
                }

                if (res != null) {
                    (res as EntityAccessor).netherPortalCooldown = res.defaultNetherPortalCooldown

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * Finds the teleportation destination block in the Nectere dimension.
     */
    private fun getTeleportTarget(
        oldEntity: Entity,
        destinationPos: BlockPos
    ): TeleportTarget {
        return TeleportTarget(
            Vec3d.of(destinationPos).add(0.5, 0.0, 0.5),
            Vec3d.ZERO,
            oldEntity.yaw,
            oldEntity.pitch
        )
    }

    /**
     * Finds the teleportation destination block when the portal location is not known.
     */
    private fun getGenericTeleportTarget(
        oldEntity: Entity,
        destination: ServerWorld
    ): TeleportTarget {
        // load chunk so heightmap loading works properly
        destination.getChunk(oldEntity.blockPos)

        val position = destination.getTopPosition(Heightmap.Type.WORLD_SURFACE, oldEntity.blockPos)
        return TeleportTarget(Vec3d.of(position).add(0.5, 0.5, 0.5), Vec3d.ZERO, oldEntity.yaw, oldEntity.pitch)
    }
}