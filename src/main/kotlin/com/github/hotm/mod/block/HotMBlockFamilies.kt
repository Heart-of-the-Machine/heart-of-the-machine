package com.github.hotm.mod.block

import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_SLAB
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_STAIRS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_STAIRS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_STAIRS
import net.minecraft.data.family.BlockFamily

object HotMBlockFamilies {
    val SMOOTH_THINKING_STONE =
        BlockFamily.Builder(HotMBlocks.SMOOTH_THINKING_STONE).stairs(SMOOTH_THINKING_STONE_STAIRS)
            .slab(SMOOTH_THINKING_STONE_SLAB).build()
    val THINKING_STONE_BRICKS =
        BlockFamily.Builder(HotMBlocks.THINKING_STONE_BRICKS).stairs(THINKING_STONE_BRICK_STAIRS)
            .slab(THINKING_STONE_BRICK_SLAB).build()
    val THINKING_STONE_TILES = BlockFamily.Builder(HotMBlocks.THINKING_STONE_TILES).stairs(THINKING_STONE_TILE_STAIRS)
        .slab(THINKING_STONE_TILE_SLAB).build()
}
