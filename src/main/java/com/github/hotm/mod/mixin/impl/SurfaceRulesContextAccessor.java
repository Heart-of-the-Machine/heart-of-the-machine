package com.github.hotm.mod.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;

@Mixin(SurfaceRules.Context.class)
public interface SurfaceRulesContextAccessor {
    @Accessor("chunkNoiseSampler")
    ChunkNoiseSampler hotm$getChunkNoiseSampler();

    @Accessor("field_37703")
    RandomState hotm$getField_37703();

    @Accessor("x")
    int hotm$getX();

    @Accessor("y")
    int hotm$getY();

    @Accessor("z")
    int hotm$getZ();
}
