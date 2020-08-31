package com.github.hotm.util

import com.github.hotm.mixin.DecoratorContextAccessor
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.ChunkRegion
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.WorldAccess
import net.minecraft.world.gen.decorator.DecoratorContext

object WorldUtils {
    fun getWorld(context: DecoratorContext): StructureWorldAccess {
        return (context as DecoratorContextAccessor).world
    }

    fun getServerWorld(context: DecoratorContext): ServerWorld? {
        return getServerWorld((context as DecoratorContextAccessor).world)
    }

    fun getServerWorld(world: WorldAccess): ServerWorld? {
        return when (world) {
            is ServerWorld -> world
            is ChunkRegion -> world.toServerWorld()
            else -> null
        }
    }
}