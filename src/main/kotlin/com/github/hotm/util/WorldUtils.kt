package com.github.hotm.util

import net.minecraft.server.world.ServerWorld
import net.minecraft.world.ChunkRegion
import net.minecraft.world.WorldAccess

object WorldUtils {
    /**
     * Converts a WorldAccess into a server world if possible.
     */
    fun getServerWorld(world: WorldAccess): ServerWorld? {
        return when (world) {
            is ServerWorld -> world
            is ChunkRegion -> world.toServerWorld()
            else -> null
        }
    }
}