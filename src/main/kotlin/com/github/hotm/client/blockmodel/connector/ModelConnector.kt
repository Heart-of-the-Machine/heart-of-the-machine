package com.github.hotm.client.blockmodel.connector

import com.github.hotm.client.HotMClientRegistries
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

interface ModelConnector {
    companion object {
        val CODEC = HotMClientRegistries.BLOCK_MODEL_CONNECTOR
        val DEFAULT = IdentityModelConnector
    }

    fun canConnect(blockView: BlockRenderView, pos: BlockPos, block: BlockState, other: BlockState): Boolean
}