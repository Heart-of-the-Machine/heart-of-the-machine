package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

/**
 * Handles adding custom dimensions to the default dimension lineup.
 */
@SuppressWarnings("unused")
@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "method_28517", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onMethod_28517(long seed, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir,
                                       SimpleRegistry<DimensionOptions> simpleRegistry) {
        DimensionAdditions.setupDimensionOptions(seed, simpleRegistry);
    }

    @Inject(method = "addRegistryDefaults", at = @At("RETURN"))
    private static void onAddRegistryDefaults(RegistryTracker.Modifiable modifiable,
                                              CallbackInfoReturnable<RegistryTracker.Modifiable> cir) {
        DimensionAdditions.setupDimensionTypes(modifiable);
    }

    @Inject(method = "getSaveDirectory", at = @At("HEAD"), cancellable = true)
    private static void onGetSaveDirectory(RegistryKey<World> worldRef, File root, CallbackInfoReturnable<File> cir) {
        if (DimensionAdditions.containsSaveDir(worldRef)) {
            cir.setReturnValue(DimensionAdditions.getSaveDir(worldRef, root));
        }
    }
}
