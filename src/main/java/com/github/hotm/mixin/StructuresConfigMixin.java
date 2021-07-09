package com.github.hotm.mixin;

import com.github.hotm.mixinapi.FeatureAdditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dirty mixin to add default structure configs when the StructuresConfig is initialized.
 */
// TODO: Use FAPI Structure-API instead
@Mixin(StructuresConfig.class)
public class StructuresConfigMixin {
    @Shadow
    @Final
    @Mutable
    private static ImmutableMap<StructureFeature<?>, StructureConfig> DEFAULT_STRUCTURES;

    /**
     * When a new StructuresConfig is created, it copies all the values from DEFAULT_STRUCTURES. This unfortunately
     * means that this mod's additions to the DEFAULT_STRUCTURES field must be hard-coded because there is not guarantee
     * that this mod will have initialized by the time the first StructuresConfigs are created.
     *
     * @param ci (unused) callback information.
     */
    @Inject(method = "<clinit>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/util/registry/Registry;STRUCTURE_FEATURE:Lnet/minecraft/util/registry/Registry;", ordinal = 0))
    private static void onClinit(CallbackInfo ci) {
        DEFAULT_STRUCTURES = FeatureAdditions.addDefaultStructureConfigs(DEFAULT_STRUCTURES);
    }
}
