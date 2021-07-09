package com.github.hotm.mixinapi;

import com.github.hotm.mixin.StairsBlockAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * Creators for blocks.
 */
public class BlockCreators {
    public static StairsBlock createStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        return StairsBlockAccessor.create(baseBlockState, settings);
    }
}
