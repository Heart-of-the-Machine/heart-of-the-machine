package com.github.hotm.mixin;

import com.github.hotm.HotMBlockTags;
import com.github.hotm.mixinapi.EntityClimbing;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

    @Inject(method = "applyClimbingSpeed", at = @At("HEAD"), cancellable = true)
    private void onApplyClimbingSpeed(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        BlockState blockState = self.getBlockStateAtPos();
        if (self.isClimbing() && HotMBlockTags.INSTANCE.getSCAFFOLDING().contains(blockState.getBlock())) {
            self.fallDistance = 0.0F;
            double d = MathHelper.clamp(motion.x, -0.15000000596046448D, 0.15000000596046448D);
            double e = MathHelper.clamp(motion.z, -0.15000000596046448D, 0.15000000596046448D);
            double g = Math.max(motion.y, -0.15000000596046448D);
            cir.setReturnValue(new Vec3d(d, g, e));
        }
    }
}
