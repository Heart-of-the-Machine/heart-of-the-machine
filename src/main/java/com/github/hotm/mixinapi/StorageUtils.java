package com.github.hotm.mixinapi;

import com.github.hotm.mixin.StorageIoWorkerInvoker;
import com.github.hotm.world.auranet.AuraNetStorage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.storage.StorageIoWorker;

import java.io.File;

public class StorageUtils {
    public static StorageIoWorker newStorageIoWorker(File file, boolean bl, String string) {
        return StorageIoWorkerInvoker.create(file, bl, string);
    }

    public static AuraNetStorage getAuraNetStorage(ServerWorld world) {
        return ((AuraNetStorageAccess) world.getChunkManager().threadedAnvilChunkStorage).hotm_getAuraNetStorage();
    }
}
