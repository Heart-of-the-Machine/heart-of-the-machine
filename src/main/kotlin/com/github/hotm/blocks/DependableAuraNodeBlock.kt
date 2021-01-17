package com.github.hotm.blocks

import com.github.hotm.util.DimBlockPos
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface DependableAuraNodeBlock : AuraNodeBlock {
    fun getDependants(state: BlockState, world: ServerWorld, pos: BlockPos): Collection<DimBlockPos>
}