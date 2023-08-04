package com.github.hotm.mod.block

import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.node.aura.SimpleSiphonAuraBlockNode
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import com.kneelawk.graphlib.api.graph.user.BlockNode

class SimpleSiphonAuraNodeBlock(settings: Settings) : Block(settings), AuraNodeBlock {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override fun getBlockNodes(world: ServerWorld, pos: BlockPos, state: BlockState): Collection<BlockNode> =
        listOf(SimpleSiphonAuraBlockNode)

    override fun getSelectedBlockNode(context: ItemUsageContext): BlockNode = SimpleSiphonAuraBlockNode

    override fun prepare(state: BlockState, world: WorldAccess, pos: BlockPos, flags: Int, maxUpdateDepth: Int) {
        if (world is ServerWorld) {
            HotMUniverses.NETWORKS.getServerGraphWorld(world).updateNodes(pos)
        }
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPE
}
