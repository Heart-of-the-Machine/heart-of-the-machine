package com.github.hotm.mixin;

import com.github.hotm.mixinapi.AuraNetStorageAccess;
import com.github.hotm.world.auranet.AuraNetStorage;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements AuraNetStorageAccess {
    private AuraNetStorage auraNetStorage;

    @Shadow
    @Final
    private File saveDir;

    @Shadow
    @Final
    private ServerWorld world;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(ServerWorld serverWorld, LevelStorage.Session session, DataFixer dataFixer,
                          StructureManager structureManager, Executor workerExecutor,
                          ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider,
                          ChunkGenerator chunkGenerator,
                          WorldGenerationProgressListener worldGenerationProgressListener,
                          Supplier<PersistentStateManager> supplier, int i, boolean bl, CallbackInfo ci) {
        auraNetStorage = new AuraNetStorage(new File(saveDir, "hotm/auranet"), dataFixer, bl);
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;close()V", shift = At.Shift.AFTER))
    private void onClose(CallbackInfo ci) throws IOException {
        auraNetStorage.close();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;tick(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        world.getProfiler().swap("aura_net");
        auraNetStorage.tick(shouldKeepTicking);
    }

    @Inject(method = "save(Lnet/minecraft/world/chunk/Chunk;)Z", at = @At("HEAD"))
    private void onSave(Chunk chunk, CallbackInfoReturnable<Boolean> cir) {
        auraNetStorage.method_20436(chunk.getPos());
    }

    @Override
    public AuraNetStorage hotm_getAuraNetStorage() {
        return auraNetStorage;
    }
}
