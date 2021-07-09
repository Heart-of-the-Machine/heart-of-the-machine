package com.github.hotm.mixin;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureWeightSampler.class)
public interface StructureWeightSamplerAccessor {
    @Invoker("<init>")
    static StructureWeightSampler create(StructureAccessor accessor, Chunk chunk) {
        throw new IllegalStateException("StructureWeightSamplerAccessor mixin error");
    }

    @Invoker
    double callGetWeight(int x, int y, int z);
}
