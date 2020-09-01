package com.github.hotm.mixin;

import com.github.hotm.gen.HotMBiomes;
import net.minecraft.world.biome.BuiltinBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinBiomes.class)
public class BuiltinBiomesMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        HotMBiomes.INSTANCE.register();
    }
}
