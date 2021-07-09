package com.github.hotm.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin to allow construction of stairs blocks.
 */
@Mixin(StairsBlock.class)
public interface StairsBlockAccessor {
    @Invoker("<init>")
    static StairsBlock create(BlockState baseBlockState, AbstractBlock.Settings settings) {
        throw new RuntimeException("StairsBlockInvoker mixin was not mixed in properly");
    }
}
