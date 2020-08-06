package com.github.hotm.gen.feature.segment

import net.minecraft.block.BlockState

/**
 * Represents a block placement.
 *
 * @param state the block state to be placed.
 * @param replaceTerrain whether the placer should replace existing blocks.
 * @param flags placement flags passed to the world.
 * @param priority this placement's priority when placing a block here. Higher priorities are replaced with lower ones.
 */
data class BlockPlacement(val state: BlockState, val replaceTerrain: Boolean, val flags: Int, val priority: Int)