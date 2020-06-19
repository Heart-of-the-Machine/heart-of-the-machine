package com.github.hotm.mixin;

import com.github.hotm.mixinopts.DimensionAdditions;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to get minecraft to stop complaining about custom dimensions.
 */
@SuppressWarnings("unused")
@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin {
    @Inject(method = "method_29567", at = @At("RETURN"), cancellable = true)
    private static void onMethod_29567(long seed, SimpleRegistry<DimensionOptions> simpleRegistry,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            cir.setReturnValue(DimensionAdditions.containsOnlyValidKeys(simpleRegistry));
        }
    }
}
