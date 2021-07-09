package com.github.hotm.mixin;

import com.github.hotm.mixinapi.FeatureAdditions;
import net.minecraft.world.biome.GenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Use FAPI Biome-API instead
@SuppressWarnings("unused")
@Mixin(GenerationSettings.Builder.class)
public class GenerationSettingsBuilderMixin {
    @Inject(method = "build", at = @At("HEAD"))
    private void onBuild(CallbackInfoReturnable<GenerationSettings> cir) {
        GenerationSettings.Builder self = (GenerationSettings.Builder) (Object) this;
        FeatureAdditions.addUbiquitousGenerationSettings(self);
    }
}
