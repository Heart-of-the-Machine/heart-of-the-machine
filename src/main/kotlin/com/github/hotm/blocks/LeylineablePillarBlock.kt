package com.github.hotm.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.PillarBlock

class LeylineablePillarBlock(private val leylineBlock: PillarBlock, settings: Settings) : PillarBlock(settings),
    Leylineable {
    override fun getLeyline(blockState: BlockState): BlockState {
        return leylineBlock.defaultState.with(AXIS, blockState[AXIS])
    }
}