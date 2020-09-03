package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
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
 * <p>
 * This is only useful for retroactively generating the nectere dimension in a pre-existing world.
 */
@SuppressWarnings("unused")
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    @Final
    protected DynamicRegistryManager.Impl registryManager;

    @Shadow
    @Final
    protected SaveProperties saveProperties;

    @Inject(method = "createWorlds", at = @At("HEAD"))
    private void onCreateWorlds(WorldGenerationProgressListener listener, CallbackInfo ci) {
        GeneratorOptions options = saveProperties.getGeneratorOptions();
        DimensionAdditions
                .setupDimensionOptions(registryManager.getDimensionTypes(), registryManager.get(Registry.BIOME_KEY),
                        registryManager.get(Registry.NOISE_SETTINGS_WORLDGEN), options.getSeed(),
                        options.getDimensions(), "existing world");
    }
}
