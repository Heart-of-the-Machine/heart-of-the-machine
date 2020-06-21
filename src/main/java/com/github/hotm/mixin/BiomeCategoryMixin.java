package com.github.hotm.mixin;

import com.github.hotm.mixinopts.BiomeCategories;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used to construct new biome category variants.
 */
@SuppressWarnings("unused")
@Mixin(Biome.Category.class)
public abstract class BiomeCategoryMixin implements StringIdentifiable {
    @Shadow
    @Final
    @Mutable
    private static Biome.Category[] field_9373;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        Biome.Category[] newValues = new Biome.Category[field_9373.length + 1];
        System.arraycopy(field_9373, 0, newValues, 0, field_9373.length);
        Biome.Category nectere = create("NECTERE", field_9373.length, "nectere");
        BiomeCategories.NECTERE = nectere;
        newValues[field_9373.length] = nectere;
        field_9373 = newValues;
    }

    @Invoker("<init>")
    private static Biome.Category create(String enumName, int ordinal, String name) {
        throw new RuntimeException("BiomeCategoryMixin was not mixed in properly");
    }
}
