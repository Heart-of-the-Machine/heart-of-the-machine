package com.github.hotm.blocks

import com.github.hotm.blockentity.CollectorDistributorAuraNodeBlockEntity
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.CollectorDistributorAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class CollectorDistributorAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlockWithEntity(settings) {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CollectorDistributorAuraNodeBlockEntity(pos, state)
    }

    override val auraNodeType: AuraNodeType<out AuraNode>
        get() = CollectorDistributorAuraNode.Type

    override fun createAuraNode(
        state: BlockState,
        world: ServerWorld,
        storage: ServerAuraNetStorage,
        pos: BlockPos
    ): AuraNode {
        return CollectorDistributorAuraNode(
            storage,
            storage.getUpdateListener(ChunkSectionPos.from(pos)),
            pos,
            0,
            listOf(),
            listOf()
        )
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }
}