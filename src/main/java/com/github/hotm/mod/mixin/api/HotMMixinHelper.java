package com.github.hotm.mod.mixin.api;

import java.util.Set;

import com.github.hotm.mod.mixin.impl.PointOfInterestTypesAccessor;
import com.github.hotm.mod.mixin.impl.SurfaceRulesContextAccessor;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import net.minecraft.world.poi.PointOfInterestType;

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

    public static PointOfInterestType registerPointOfInterest(Registry<PointOfInterestType> registry,
                                                              RegistryKey<PointOfInterestType> key,
                                                              Set<BlockState> states, int ticketCount,
                                                              int searchDistance) {
        return PointOfInterestTypesAccessor.callRegister(registry, key, states, ticketCount, searchDistance);
    }
}
