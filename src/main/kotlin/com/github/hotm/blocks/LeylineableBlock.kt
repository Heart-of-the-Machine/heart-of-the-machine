package com.github.hotm.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState


class LeylineableBlock(private val leylineBlock: Block, settings: Settings) : Block(settings), Leylineable {
    override fun getLeyline(blockState: BlockState): BlockState {
        return leylineBlock.defaultState
    }
}