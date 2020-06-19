package com.github.hotm.mixin;

import com.github.hotm.mixinopts.DimensionAdditions;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

/**
 * Mixin to get minecraft to stop complaining about custom dimensions.
 */
@SuppressWarnings("unused")
@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin {
    /**
     * Injector to verify custom dimensions and make sure minecraft doesn't complain about them.
     * <p>
     * In theory, many different mods should be able to use this exact injector without stepping on each other's toes.
     *
     * @param seed           the current seed.
     * @param simpleRegistry the registry containing the dimensions.
     * @param cir            used to signal a dimension verification failure.
     * @param list           the list minecraft checks for dimension customizations.
     */
    @Inject(method = "method_29567", at = @At(value = "INVOKE_ASSIGN",
            target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void onMethod_29567_2(long seed, SimpleRegistry<DimensionOptions> simpleRegistry,
                                         CallbackInfoReturnable<Boolean> cir,
                                         List<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list) {
        if (!DimensionAdditions.checkAndRemoveDimensions(seed, list)) {
            cir.setReturnValue(false);
        }
    }
}
