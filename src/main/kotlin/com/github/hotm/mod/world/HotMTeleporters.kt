package com.github.hotm.mod.world

import com.github.hotm.mod.Constants
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget

object HotMTeleporters {
    enum class Result {
        SUCCESS,
        COOLDOWN,
        FAILURE
    }

    fun attemptNectereTeleportation(
        entity: Entity, world: ServerWorld, portalPos: BlockPos, callback: (Result) -> Unit
    ) {
        if (entity.hasNetherPortalCooldown()) {
            entity.resetNetherPortalCooldown()

            callback(Result.COOLDOWN)
        } else {
            entity.resetNetherPortalCooldown()
            (entity as? ServerPlayerEntity)?.sendMessage(Constants.msg("teleporting"), true)

            val server = world.server

            if (world.registryKey == HotMDimensions.NECTERE_KEY) {
                val newWorld = HotMLocationConversions.nectere2NonWorldC(world, portalPos)
                if (newWorld == null) {
                    callback(Result.FAILURE)
                    return
                }

                HotMPortalFinders.findOrCreateNonNecterePortal(world, portalPos, newWorld) { destPos ->
                    if (destPos == null) {
                        callback(Result.FAILURE)
                        return@findOrCreateNonNecterePortal
                    }

                    val resEntity = QuiltDimensions.teleport<Entity>(
                        entity,
                        newWorld,
                        TeleportTarget(Vec3d.ofBottomCenter(destPos), Vec3d.ZERO, entity.yaw, entity.pitch)
                    )

                    resEntity?.resetNetherPortalCooldown()

                    callback(Result.SUCCESS)
                }
            } else {
                val newWorld = HotMDimensions.getNectere(server)
                HotMPortalFinders.findOrCreateNecterePortal(world, portalPos, newWorld) { destPos ->
                    if (destPos == null) {
                        callback(Result.FAILURE)
                        return@findOrCreateNecterePortal
                    }

                    val resEntity = QuiltDimensions.teleport<Entity>(
                        entity,
                        newWorld,
                        TeleportTarget(Vec3d.ofBottomCenter(destPos), Vec3d.ZERO, entity.yaw, entity.pitch)
                    )

                    resEntity?.resetNetherPortalCooldown()

                    callback(Result.SUCCESS)
                }
            }
        }
    }
}
