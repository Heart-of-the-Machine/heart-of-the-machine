package com.github.hotm.mixinapi;

import com.github.hotm.mixin.StructureWeightSamplerAccessor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;

public class StructureWeightSamplerAccess {
    public static StructureWeightSampler create(StructureAccessor accessor, Chunk chunk) {
        return StructureWeightSamplerAccessor.create(accessor, chunk);
    }

    public static double getWeight(StructureWeightSampler sampler, int x, int y, int z) {
        return ((StructureWeightSamplerAccessor) sampler).callGetWeight(x, y, z);
    }
}
