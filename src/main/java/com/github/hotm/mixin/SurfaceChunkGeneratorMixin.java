package com.github.hotm.mixin;

import com.github.hotm.hacks.ChunkGeneratorSeedHack;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Terrible hack to get the seed of the currently open world.
 */
@Mixin(SurfaceChunkGenerator.class)
public class SurfaceChunkGeneratorMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(BiomeSource source, long seed, ChunkGeneratorType type, CallbackInfo ci) {
        ChunkGeneratorSeedHack.seed = seed;

        // When you're too lazy to use a debugger...
//        System.out.println("#################################");
//        System.out.println("SurfaceChunkGenerator constructed. Seed: " + seed + " Stack trace:");
//        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
//            System.out.println(e);
//        }
//        System.out.println("#################################");
    }
}
