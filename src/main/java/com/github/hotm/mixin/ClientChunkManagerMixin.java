package com.github.hotm.mixin;

import com.github.hotm.mixinapi.ClientMetaStorageAccess;
import com.github.hotm.world.meta.client.ClientMetaStorage;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin implements ClientMetaStorageAccess {
    private ClientMetaStorage hotm_metaStorage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(ClientWorld world, int loadDistance, CallbackInfo ci) {
        hotm_metaStorage = new ClientMetaStorage(world, ClientMetaStorage.getChunkMapRadius(loadDistance));
    }

    @Inject(method = "unload", at = @At("RETURN"))
    private void onUnload(int chunkX, int chunkZ, CallbackInfo ci) {
        hotm_metaStorage.unload(chunkX, chunkZ);
    }

    @Inject(method = "setChunkMapCenter", at = @At("RETURN"))
    private void onSetChunkMapCenter(int x, int z, CallbackInfo ci) {
        hotm_metaStorage.updateCenterChunk(x, z);
    }

    @Inject(method = "updateLoadDistance", at = @At("RETURN"))
    private void onUpdateLoadDistance(int loadDistance, CallbackInfo ci) {
        hotm_metaStorage =
                hotm_metaStorage.withLoadDistance(ClientMetaStorage.getChunkMapRadius(loadDistance));
    }

    @Override
    public ClientMetaStorage hotm_getMetaStorage() {
        return hotm_metaStorage;
    }
}
