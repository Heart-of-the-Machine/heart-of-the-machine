package com.github.hotm.blocks

import com.github.hotm.blockentity.AbstractDependableAuraNodeBlockEntity
import com.github.hotm.blockentity.HotMBlockEntities
import com.github.hotm.blockentity.PortalReceiverAuraNodeBlockEntity
import com.github.hotm.auranet.AuraNode
import com.github.hotm.auranet.AuraNodeType
import com.github.hotm.auranet.PortalAuraNodeUtils
import com.github.hotm.auranet.PortalReceiverAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
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

class PortalReceiverAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlockWithEntity(settings) {
    companion object {
        private val SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PortalReceiverAuraNodeBlockEntity(pos, state)
    }

    override val auraNodeType: AuraNodeType<out AuraNode>
        get() = PortalReceiverAuraNode.Type

    override fun createAuraNode(
        state: BlockState,
        world: ServerWorld,
        storage: ServerAuraNetStorage,
        pos: BlockPos
    ): AuraNode {
        return PortalReceiverAuraNode(
            storage,
            storage.getUpdateListener(pos),
            pos,
            0f,
            null,
            PortalAuraNodeUtils.isPortalStructureValid(pos, world)
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
            HotMBlockEntities.PORTAL_RECEIVER_AURA_NODE,
            if (world.isClient) null else AbstractDependableAuraNodeBlockEntity.Companion::tickServer
        )
    }
}