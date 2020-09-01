package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void onGetTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        if (DimensionAdditions.shouldUseCustomPlacer(destination)) {
            cir.setReturnValue(DimensionAdditions.useCustomPlacer((Entity) (Object) this, destination));
        }
    }
}
