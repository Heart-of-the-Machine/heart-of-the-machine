package com.github.hotm.client.blockmodel.connector

import com.github.hotm.HotMBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

object LeylineModelConnector : ModelConnector {
    val LEYLINE_BLOCKS = hashSetOf<Block>()

    init {
        LEYLINE_BLOCKS.add(HotMBlocks.MACHINE_CASING_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.PLASSEIN_GRASS_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.PLASSEIN_STEM_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.RUSTED_MACHINE_CASING_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.SURFACE_MACHINE_CASING_LEYLINE)
        LEYLINE_BLOCKS.add(HotMBlocks.THINKING_STONE_LEYLINE)
    }

    override fun canConnect(blockView: BlockRenderView, pos: BlockPos, block: BlockState, other: BlockState): Boolean {
        return LEYLINE_BLOCKS.contains(other.block)
    }
}