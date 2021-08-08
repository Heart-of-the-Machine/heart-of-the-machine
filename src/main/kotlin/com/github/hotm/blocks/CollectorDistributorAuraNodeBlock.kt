package com.github.hotm.blocks

import com.github.hotm.blockentity.AbstractDependableAuraNodeBlockEntity
import com.github.hotm.blockentity.CollectorDistributorAuraNodeBlockEntity
import com.github.hotm.blockentity.HotMBlockEntities
import com.github.hotm.meta.MetaBlock
import com.github.hotm.meta.MetaBlockType
import com.github.hotm.meta.auranet.CollectorDistributorAuraNode
import com.github.hotm.world.meta.server.ServerMetaStorage
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CollectorDistributorAuraNodeBlock(settings: Settings) : AbstractBlockWithMetaAndEntity(settings) {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CollectorDistributorAuraNodeBlockEntity(pos, state)
    }

    override val metaBlockType: MetaBlockType<out MetaBlock>
        get() = CollectorDistributorAuraNode.Type

    override fun createMetaBlock(
        state: BlockState,
        world: ServerWorld,
        storage: ServerMetaStorage,
        pos: BlockPos
    ): MetaBlock {
        return CollectorDistributorAuraNode(
            storage,
            storage.getUpdateListener(ChunkSectionPos.from(pos)),
            pos,
            0f,
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

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type,
            HotMBlockEntities.COLLECTOR_DISTRIBUTOR_AURA_NODE,
            if (world.isClient) null else AbstractDependableAuraNodeBlockEntity.Companion::tickServer
        )
    }
}