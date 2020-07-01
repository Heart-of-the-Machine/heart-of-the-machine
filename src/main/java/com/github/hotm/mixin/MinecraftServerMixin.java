package com.github.hotm.mixin;

import com.github.hotm.mixinopts.DimensionAdditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle forcing the generation of the nectere dimension.
 *
 * This is only useful for retroactively generating the nectere dimension in a pre-existing world.
 */
@SuppressWarnings("unused")
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    @Final
    protected SaveProperties saveProperties;

    @Inject(method = "createWorlds", at = @At("HEAD"))
    private void onCreateWorlds(WorldGenerationProgressListener listener, CallbackInfo ci) {
        if (DimensionAdditions.shouldForceDimensions()) {
            System.out.println("################");
            System.out.println("Forcing HotM dimension loading.");
            System.out.println("################");

            GeneratorOptions options = saveProperties.getGeneratorOptions();
            DimensionAdditions.setupDimensionOptions(options.getSeed(), options.getDimensionMap());
        }
    }
}
