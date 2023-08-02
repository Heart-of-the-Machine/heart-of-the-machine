package com.github.hotm.mod.node.aura

import com.github.hotm.mod.block.AuraNodeBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.graph.user.BlockNodeDiscoverer

object AuraNodeBlockDiscoverer : BlockNodeDiscoverer {
    override fun getNodesInBlock(world: ServerWorld, pos: BlockPos): Collection<BlockNode> {
        val state = world.getBlockState(pos)
        val block = state.block
        return if (block is AuraNodeBlock) block.getBlockNodes(world, pos, state) else emptyList()
    }
}
