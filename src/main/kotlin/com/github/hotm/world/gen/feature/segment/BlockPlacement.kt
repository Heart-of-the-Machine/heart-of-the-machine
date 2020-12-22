package com.github.hotm.world.gen.feature.segment

import net.minecraft.block.BlockState

/**
 * Represents a block placement.
 *
 * @param state the block state to be placed.
 * @param replaceTerrain whether the placer should replace existing blocks.
 * @param flags placement flags passed to the world.
 * @param priority this placement's priority when placing a block here. Higher priorities are replaced with lower ones.
 * @param leafPlacement how this block placement should be handled in terms of leaf decay and distance from sources (logs).
 */
data class BlockPlacement(
    val state: BlockState,
    val replaceTerrain: Boolean,
    val flags: Int,
    val priority: Int,
    val leafPlacement: LeafPlacement
) {
    constructor(state: BlockState, replaceTerrain: Boolean, flags: Int, priority: Int) : this(
        state,
        replaceTerrain,
        flags,
        priority,
        LeafPlacement.NONE
    )
}
