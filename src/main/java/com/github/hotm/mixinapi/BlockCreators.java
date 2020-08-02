package com.github.hotm.mixinapi;

import com.github.hotm.mixin.StairsBlockInvoker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * Creators for blocks.
 */
public class BlockCreators {
    public static StairsBlock createStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        return StairsBlockInvoker.create(baseBlockState, settings);
    }
}
