package com.github.hotm.util

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.*

object WorldUtils {
    /**
     * Converts a WorldAccess into a server world if possible.
     */
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
                    BlockPos(contextx.end)
                )
            }
        )
    }
}