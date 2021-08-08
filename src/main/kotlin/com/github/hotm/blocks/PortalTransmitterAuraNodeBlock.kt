package com.github.hotm.blocks

import com.github.hotm.blockentity.HotMBlockEntities
import com.github.hotm.blockentity.PortalTransmitterAuraNodeBlockEntity
import com.github.hotm.meta.MetaBlock
import com.github.hotm.meta.MetaBlockType
import com.github.hotm.meta.auranet.PortalAuraNodeUtils
import com.github.hotm.meta.auranet.PortalTransmitterAuraNode
import com.github.hotm.world.meta.server.ServerMetaStorage
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class PortalTransmitterAuraNodeBlock(settings: Settings) : AbstractBlockWithMetaAndEntity(settings) {
    companion object {
        private val SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0)
    }

    override val metaBlockType: MetaBlockType<out MetaBlock>
        get() = PortalTransmitterAuraNode.Type

    override fun createMetaBlock(
        state: BlockState,
        world: ServerWorld,
        storage: ServerMetaStorage,
        pos: BlockPos
    ): MetaBlock {
        return PortalTransmitterAuraNode(
            storage,
            storage.getUpdateListener(pos),
            pos,
            0f,
            listOf(),
            PortalAuraNodeUtils.isPortalStructureValid(pos, world)
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PortalTransmitterAuraNodeBlockEntity(pos, state)
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
            HotMBlockEntities.PORTAL_TRANSMITTER_AURA_NODE,
            if (world.isClient) null else PortalTransmitterAuraNodeBlockEntity.Companion::tickServer
        )
    }
}