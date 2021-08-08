package com.github.hotm.mixin;

import com.github.hotm.mixinapi.ServerMetaStorageAccess;
import com.github.hotm.mixinapi.StorageUtils;
import com.github.hotm.net.HotMNetwork;
import com.github.hotm.world.meta.server.ServerMetaStorage;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Used to allow the meta block to be saved and loaded properly.
 */
@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements ServerMetaStorageAccess {
    private ServerMetaStorage hotm_metaStorage;

    @Shadow
    @Final
    ServerWorld world;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer,
                          StructureManager structureManager, Executor executor,
                          ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider,
                          ChunkGenerator chunkGenerator,
                          WorldGenerationProgressListener worldGenerationProgressListener,
                          ChunkStatusChangeListener chunkStatusChangeListener,
                          Supplier<PersistentStateManager> persistentStateManagerFactory, int viewDistance,
                          boolean dsync, CallbackInfo ci) {
        hotm_metaStorage = new ServerMetaStorage(world,
                StorageUtils.setupMetaDir(session.getWorldDirectory(world.getRegistryKey())), dataFixer, dsync);
    }

    @Inject(method = "close",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;close()V",
                    shift = At.Shift.AFTER))
    private void onClose(CallbackInfo ci) throws IOException {
        hotm_metaStorage.close();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/poi/PointOfInterestStorage;tick(Ljava/util/function/BooleanSupplier;)V",
            shift = At.Shift.AFTER))
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        world.getProfiler().swap("meta_storage");
        hotm_metaStorage.tick(shouldKeepTicking);
    }

    /*
     * This is a lambda mixin. :/
     */
    @Inject(method = "method_17256", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/ChunkSerializer;deserialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/world/poi/PointOfInterestStorage;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/world/chunk/ProtoChunk;"))
    private void onLoadChunkStartDeserialize(ChunkPos pos,
                                             CallbackInfoReturnable<Either<Chunk, ChunkHolder.Unloaded>> cir) {
        StorageUtils.startDeserialize(hotm_metaStorage);
    }

    /*
     * This is a lambda mixin. :/
     */
    @Inject(method = "method_17256", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/ChunkSerializer;deserialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/world/poi/PointOfInterestStorage;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/world/chunk/ProtoChunk;",
            shift = At.Shift.AFTER))
    private void onLoadChunkEndDeserialize(ChunkPos pos,
                                           CallbackInfoReturnable<Either<Chunk, ChunkHolder.Unloaded>> cir) {
        StorageUtils.endDeserialize();
    }

    @Inject(method = "save(Lnet/minecraft/world/chunk/Chunk;)Z", at = @At("HEAD"))
    private void onSave(Chunk chunk, CallbackInfoReturnable<Boolean> cir) {
        hotm_metaStorage.trySave(chunk.getPos());
    }

    @Inject(method = "sendChunkDataPackets", at = @At("RETURN"))
    private void onSendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk,
                                        CallbackInfo ci) {
        HotMNetwork.sendMetaChunkPillar(hotm_metaStorage, player, chunk.getPos());
    }

    @Override
    public ServerMetaStorage hotm_getMetaStorage() {
        return hotm_metaStorage;
    }
}
