package com.github.hotm.client.blockmodel.connector

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

object IdentityModelConnector : ModelConnector {

    override fun canConnect(blockView: BlockRenderView, pos: BlockPos, block: BlockState, other: BlockState): Boolean {
        return other.isOf(block.block)
    }
}