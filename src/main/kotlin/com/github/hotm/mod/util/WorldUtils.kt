package com.github.hotm.mod.util

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockStateRaycastContext
import net.minecraft.world.BlockView
import net.minecraft.world.ChunkRegion
import net.minecraft.world.World
import net.minecraft.world.WorldView

object WorldUtils {
    /**
     * Converts a WorldAccess into a server world if possible.
     */
    @Deprecated(message = "Using this usually means you're doing something that could cause deadlocks")
    fun getServerWorld(world: WorldView): ServerWorld? {
        return when (world) {
            is ServerWorld -> world
            is ChunkRegion -> world.toServerWorld()
            else -> null
        }
    }

    /**
     * Performs a block raycast through loaded chunks, accounting for block collision shapes.
     */
    fun loadedRaycast(world: World, context: BlockStateRaycastContext): BlockHitResult {
        return BlockView.raycast(context.start, context.end, context,
            { contextx: BlockStateRaycastContext, pos: BlockPos ->
                if (!world.isChunkLoaded(pos)) {
                    null
                } else {
                    val state = world.getBlockState(pos)

                    if (contextx.statePredicate.test(state)) {
                        val start = contextx.start
                        val end = contextx.end

                        val voxelShape = state.getCollisionShape(world, pos)

                        world.raycastBlock(start, end, pos, voxelShape, state)
                    } else {
                        null
                    }
                }
            }, { contextx: BlockStateRaycastContext ->
                val offset = contextx.start.subtract(contextx.end)

                BlockHitResult.createMissed(
                    contextx.end,
                    Direction.getFacing(offset.x, offset.y, offset.z),
                    BlockPos.create(contextx.end.x, contextx.end.y, contextx.end.z)
                )
            }
        )
    }
}
