package com.github.hotm.mod.mixin.api;

import com.github.hotm.mod.mixin.impl.SurfaceRulesContextAccessor;

import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;

public class HotMMixinHelper {
    public static ChunkNoiseSampler getChunkNoiseSampler(SurfaceRules.Context ctx) {
        return ((SurfaceRulesContextAccessor) (Object) ctx).hotm$getChunkNoiseSampler();
    }

    public static RandomState getRandomState(SurfaceRules.Context ctx) {
        return ((SurfaceRulesContextAccessor) (Object) ctx).hotm$getField_37703();
    }

    public static int getX(SurfaceRules.Context ctx) {
        return ((SurfaceRulesContextAccessor) (Object) ctx).hotm$getX();
    }

    public static int getY(SurfaceRules.Context ctx) {
        return ((SurfaceRulesContextAccessor) (Object) ctx).hotm$getY();
    }

    public static int getZ(SurfaceRules.Context ctx) {
        return ((SurfaceRulesContextAccessor) (Object) ctx).hotm$getZ();
    }
}
