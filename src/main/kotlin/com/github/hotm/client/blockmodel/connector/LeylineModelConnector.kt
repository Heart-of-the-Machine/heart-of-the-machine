package com.github.hotm.client.blockmodel.connector

import com.github.hotm.blocks.HotMBlocks
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

object LeylineModelConnector : ModelConnector {
    override fun canConnect(blockView: BlockRenderView, pos: BlockPos, block: BlockState, other: BlockState): Boolean {
        return HotMBlocks.isLeyline(other.block)
    }
}