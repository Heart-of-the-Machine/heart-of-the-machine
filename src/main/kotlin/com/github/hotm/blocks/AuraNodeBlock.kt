package com.github.hotm.blocks

import com.github.hotm.util.DimBlockPos
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface AuraNodeBlock {
    fun updateAll(state: BlockState, world: ServerWorld, pos: BlockPos) {
        reconnect(state, world, pos, hashSetOf())
        recalculate(state, world, pos, hashSetOf())
    }

    fun reconnect(state: BlockState, world: ServerWorld, pos: BlockPos, previousNodes: Set<DimBlockPos>)

    fun recalculate(state: BlockState, world: ServerWorld, pos: BlockPos, previousNodes: Set<DimBlockPos>)
}