package com.github.hotm.mixin;

import com.github.hotm.mixinapi.StructureAdditions;
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
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        DEFAULT_STRUCTURES = StructureAdditions.addDefaultStructureConfigs(DEFAULT_STRUCTURES);
    }
}
