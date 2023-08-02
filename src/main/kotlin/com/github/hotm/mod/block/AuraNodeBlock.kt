package com.github.hotm.mod.block

import net.minecraft.block.BlockState
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import com.kneelawk.graphlib.api.graph.user.BlockNode

interface AuraNodeBlock {
    fun getBlockNodes(world: ServerWorld, pos: BlockPos, state: BlockState): Collection<BlockNode>

    fun getSelectedBlockNode(context: ItemUsageContext): BlockNode
}
