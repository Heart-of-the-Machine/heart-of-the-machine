package com.github.hotm.blocks

import com.github.hotm.blockentity.DebugTickingBlockEntity
import com.github.hotm.blockentity.HotMBlockEntities
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DebugTickingBlock(settings: Settings) : BlockWithEntity(settings) {
    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(type, HotMBlockEntities.DEBUG_TICKING) { _, _, _, be ->
            be.tick()
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DebugTickingBlockEntity(pos, state)
    }
}