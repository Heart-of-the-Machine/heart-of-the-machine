package com.github.hotm.mixinapi;

import com.github.hotm.misc.HotMLog;
import com.github.hotm.mixin.StorageIoWorkerInvoker;
import com.github.hotm.world.auranet.AuraNetAccess;
import com.github.hotm.world.auranet.client.ClientAuraNetStorage;
import com.github.hotm.world.auranet.server.ServerAuraNetStorage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.StorageIoWorker;

import java.io.File;

public class StorageUtils {
    private static final String EXT_BE_DIR_NAME = "hotm" + File.separator + "extbe";
    private static final ThreadLocal<ServerAuraNetStorage> CURRENT_STORAGE = new ThreadLocal<>();

    public static File setupExtBEDir(File worldDir) {
        File auraNetDir = new File(worldDir, "hotm" + File.separator + "auranet");
        File extBEDir = new File(worldDir, EXT_BE_DIR_NAME);

        if (auraNetDir.exists() && !auraNetDir.renameTo(extBEDir)) {
            HotMLog.getLog()
                    .warn("Unable to rename existing aura net directory to extbe. ExtBEs (like aura nodes) will be reset.");
        }

        return extBEDir;
    }

    public static void startDeserialize(ServerAuraNetStorage storage) {
        CURRENT_STORAGE.set(storage);
    }

    public static void handleDeserialize(ChunkPos pos, ChunkSection section) {
        ServerAuraNetStorage storage = CURRENT_STORAGE.get();
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

    public static ServerAuraNetStorage getServerAuraNetStorage(ServerWorld world) {
        return ((ServerAuraNetStorageAccess) world.getChunkManager().threadedAnvilChunkStorage)
                .hotm_getAuraNetStorage();
    }

    public static ClientAuraNetStorage getClientAuraNetStorage(ClientWorld world) {
        return ((ClientAuraNetStorageAccess) world.getChunkManager()).hotm_getAuraNetStorage();
    }

    public static AuraNetAccess getAuraNetAccess(World world) {
        if (world.isClient) {
            return getClientAuraNetStorage((ClientWorld) world);
        } else {
            return getServerAuraNetStorage((ServerWorld) world);
        }
    }
}
