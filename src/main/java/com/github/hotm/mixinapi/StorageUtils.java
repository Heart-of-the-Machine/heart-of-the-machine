package com.github.hotm.mixinapi;

import com.github.hotm.misc.HotMLog;
import com.github.hotm.mixin.StorageIoWorkerInvoker;
import com.github.hotm.world.meta.MetaAccess;
import com.github.hotm.world.meta.client.ClientMetaStorage;
import com.github.hotm.world.meta.server.ServerMetaStorage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.StorageIoWorker;

import java.io.File;

public class StorageUtils {
    private static final String META_DIR_NAME = "hotm" + File.separator + "meta";
    private static final ThreadLocal<ServerMetaStorage> CURRENT_STORAGE = new ThreadLocal<>();

    public static File setupMetaDir(File worldDir) {
        File auraNetDir = new File(worldDir, "hotm" + File.separator + "auranet");
        File metaDir = new File(worldDir, META_DIR_NAME);

        if (auraNetDir.exists() && !auraNetDir.renameTo(metaDir)) {
            HotMLog.getLog()
                    .warn("Unable to rename existing aura net directory to meta. Meta blocks (like aura nodes) will be reset.");
        }

        return metaDir;
    }

    public static void startDeserialize(ServerMetaStorage storage) {
        CURRENT_STORAGE.set(storage);
    }

    public static void handleDeserialize(ChunkPos pos, ChunkSection section) {
        ServerMetaStorage storage = CURRENT_STORAGE.get();
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

    public static ServerMetaStorage getServerMetaStorage(ServerWorld world) {
        return ((ServerMetaStorageAccess) world.getChunkManager().threadedAnvilChunkStorage)
                .hotm_getMetaStorage();
    }

    public static ClientMetaStorage getClientMetaStorage(ClientWorld world) {
        return ((ClientMetaStorageAccess) world.getChunkManager()).hotm_getMetaStorage();
    }

    public static MetaAccess getMetaAccess(World world) {
        if (world.isClient) {
            return getClientMetaStorage((ClientWorld) world);
        } else {
            return getServerMetaStorage((ServerWorld) world);
        }
    }
}
