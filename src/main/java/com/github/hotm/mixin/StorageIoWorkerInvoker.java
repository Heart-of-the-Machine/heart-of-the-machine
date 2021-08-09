package com.github.hotm.mixin;

import net.minecraft.world.storage.StorageIoWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

@Mixin(StorageIoWorker.class)
public interface StorageIoWorkerInvoker {
    @Invoker("<init>")
    static StorageIoWorker create(File file, boolean bl, String string) {
        throw new RuntimeException("StorageIoWorkerInvoker mixin was not mixed in properly!");
    }
}
