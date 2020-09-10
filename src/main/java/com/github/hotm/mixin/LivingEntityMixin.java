package com.github.hotm.mixin;

import com.github.hotm.mixinapi.EntityClimbing;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void onIsClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isSpectator() && EntityClimbing.isClimbing(self)) {
            cir.setReturnValue(true);
        }
    }
}
