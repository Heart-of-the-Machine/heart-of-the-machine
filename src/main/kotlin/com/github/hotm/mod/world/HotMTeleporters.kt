package com.github.hotm.mod.world

import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

object HotMTeleporters {
    enum class Result {
        SUCCESS,
        COOLDOWN,
        FAILURE
    }

    fun attemptNectereTeleportation(entity: Entity, world: ServerWorld, portalPos: BlockPos): Result {
        if (entity.hasNetherPortalCooldown()) {
            entity.resetNetherPortalCooldown()

            return Result.COOLDOWN
        } else {
            val server = world.server

            if (world.registryKey == HotMDimensions.NECTERE_KEY) {
                val newWorld = HotMLocationConversions.nectere2NonWorldC(world, portalPos) ?: return Result.FAILURE
                val destPos =
                    HotMPortalFinders.findOrCreateNonPortal(world, portalPos, newWorld, entity) ?: return Result.FAILURE

                val resEntity = QuiltDimensions.teleport<Entity>(
                    entity,
                    newWorld,
                    TeleportTarget(Vec3d.ofBottomCenter(destPos), Vec3d.ZERO, entity.yaw, entity.pitch)
                )

                resEntity?.resetNetherPortalCooldown()

                return Result.SUCCESS
            } else {
                val newWorld = HotMDimensions.getNectere(server)
                val destPos = HotMPortalFinders.findOrCreateNecterePortal(world, portalPos, newWorld, entity)
                    ?: return Result.FAILURE

                val resEntity = QuiltDimensions.teleport<Entity>(
                    entity,
                    newWorld,
                    TeleportTarget(Vec3d.ofBottomCenter(destPos), Vec3d.ZERO, entity.yaw, entity.pitch)
                )

                resEntity?.resetNetherPortalCooldown()

                return Result.SUCCESS
            }
        }
    }
}
