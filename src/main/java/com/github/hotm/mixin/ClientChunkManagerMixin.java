package com.github.hotm.mixin;

import com.github.hotm.mixinapi.ClientAuraNetStorageAccess;
import com.github.hotm.world.auranet.client.ClientAuraNetStorage;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin implements ClientAuraNetStorageAccess {
    private ClientAuraNetStorage hotm_auraNetStorage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(ClientWorld world, int loadDistance, CallbackInfo ci) {
        hotm_auraNetStorage = new ClientAuraNetStorage(world, ClientAuraNetStorage.getChunkMapRadius(loadDistance));
    }

    @Inject(method = "unload", at = @At("RETURN"))
    private void onUnload(int chunkX, int chunkZ, CallbackInfo ci) {
        hotm_auraNetStorage.unload(chunkX, chunkZ);
    }

    @Inject(method = "setChunkMapCenter", at = @At("RETURN"))
    private void onSetChunkMapCenter(int x, int z, CallbackInfo ci) {
        hotm_auraNetStorage.updateCenterChunk(x, z);
    }

    @Inject(method = "updateLoadDistance", at = @At("RETURN"))
    private void onUpdateLoadDistance(int loadDistance, CallbackInfo ci) {
        hotm_auraNetStorage =
                hotm_auraNetStorage.withLoadDistance(ClientAuraNetStorage.getChunkMapRadius(loadDistance));
    }

    @Override
    public ClientAuraNetStorage hotm_getAuraNetStorage() {
        return hotm_auraNetStorage;
    }
}
