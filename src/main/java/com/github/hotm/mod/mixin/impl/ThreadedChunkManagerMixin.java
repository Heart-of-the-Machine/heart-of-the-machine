package com.github.hotm.mod.mixin.impl;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.github.hotm.mod.HotMLog;
import com.github.hotm.mod.mixin.api.ServerAuraStorageAccess;
import com.github.hotm.mod.world.aura.server.ServerAuraStorage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedChunkManager;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.storage.WorldSaveStorage;

@Mixin(ThreadedChunkManager.class)
public class ThreadedChunkManagerMixin implements ServerAuraStorageAccess {
    @Shadow
    @Final
    ServerWorld world;

    @Unique
    private ServerAuraStorage auraStorage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(
        ServerWorld world,
        WorldSaveStorage.Session session,
        DataFixer dataFixer,
        StructureTemplateManager structureTemplateManager,
        Executor executor,
        ThreadExecutor<Runnable> mainThreadExecutor,
        ChunkProvider chunkProvider,
        ChunkGenerator chunkGenerator,
        WorldGenerationProgressListener worldGenerationProgressListener,
        ChunkStatusChangeListener chunkStatusChangeListener,
        Supplier<PersistentStateManager> persistentStateManagerFactory,
        int viewDistance,
        boolean dsync,
        CallbackInfo ci
    ) {
        auraStorage =
            new ServerAuraStorage(world, session.getWorldDirectory(world.getRegistryKey()).resolve("data"), dsync);
    }

    @Inject(method = "save(Z)V", at = @At("HEAD"))
    private void onSave(boolean flush, CallbackInfo ci) {
        try {
            auraStorage.saveAll();
        } catch (Exception e) {
            HotMLog.getLOG()
                .error("Error saving graph world storage. World: '{}'/{}", world, world.getRegistryKey().getValue(), e);
        }
    }

    @Override
    public ServerAuraStorage hotm_getAuraStorage() {
        return auraStorage;
    }
}
