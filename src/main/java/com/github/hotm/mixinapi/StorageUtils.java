package com.github.hotm.mixinapi;

import com.github.hotm.mixin.StorageIoWorkerInvoker;
import com.github.hotm.world.auranet.AuraNetStorage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.StorageIoWorker;

import java.io.File;

public class StorageUtils {
    private static final ThreadLocal<AuraNetStorage> CURRENT_STORAGE = new ThreadLocal<>();

    public static void startDeserialize(AuraNetStorage storage) {
        CURRENT_STORAGE.set(storage);
    }

    public static void handleDeserialize(ChunkPos pos, ChunkSection section) {
        AuraNetStorage storage = CURRENT_STORAGE.get();
        if (storage != null) {
            storage.initForPalette(pos, section);
        }
    }

    public static void endDeserialize() {
        CURRENT_STORAGE.remove();
    }

    public static StorageIoWorker newStorageIoWorker(File file, boolean bl, String string) {
        return StorageIoWorkerInvoker.create(file, bl, string);
    }

    public static AuraNetStorage getAuraNetStorage(ServerWorld world) {
        return ((AuraNetStorageAccess) world.getChunkManager().threadedAnvilChunkStorage).hotm_getAuraNetStorage();
    }
}
