package com.github.hotm.mixinapi;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

/**
 * Used in DimensionAdditions.teleport to control the resulting position of the entity.
 */
public interface EntityPlacer {
    TeleportTarget placeEntity(Entity entity, ServerWorld destination);
}
