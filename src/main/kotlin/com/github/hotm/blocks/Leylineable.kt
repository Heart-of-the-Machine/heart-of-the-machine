package com.github.hotm.blocks

import net.minecraft.block.BlockState

interface Leylineable {
    fun getLeyline(blockState: BlockState): BlockState
}