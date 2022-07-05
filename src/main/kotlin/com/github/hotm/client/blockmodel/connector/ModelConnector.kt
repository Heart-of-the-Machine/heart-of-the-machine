package com.github.hotm.client.blockmodel.connector

import com.github.hotm.client.HotMClientRegistries
import com.mojang.serialization.Codec
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

interface ModelConnector {
    companion object {
        val CODEC: Codec<ModelConnector> = HotMClientRegistries.BLOCK_MODEL_CONNECTOR.codec
        val DEFAULT = IdentityModelConnector
    }

    fun canConnect(blockView: BlockRenderView, pos: BlockPos, block: BlockState, other: BlockState): Boolean
}
