package com.github.hotm.mixin;

import net.minecraft.block.Block;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(StructurePiece.class)
public interface StructurePieceAccessor {
    @Accessor("BLOCKS_NEEDING_POST_PROCESSING")
    static Set<Block> getBlocksNeedingPostProcessing() {
        throw new RuntimeException("StructurePieceMixin was not mixed in properly!");
    }

    @Accessor
    BlockMirror getMirror();

    @Accessor
    BlockRotation getRotation();
}
