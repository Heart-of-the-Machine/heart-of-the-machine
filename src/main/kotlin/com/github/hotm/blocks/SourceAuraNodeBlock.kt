package com.github.hotm.blocks

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface SourceAuraNodeBlock : AuraNodeBlock {
    fun getSource(state: BlockState, world: ServerWorld, pos: BlockPos): Int
}