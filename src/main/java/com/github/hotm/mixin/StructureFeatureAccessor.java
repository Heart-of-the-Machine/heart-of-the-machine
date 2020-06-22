package com.github.hotm.mixin;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Accesses StructureFeature's private static fields.
 */
@Mixin(StructureFeature.class)
public interface StructureFeatureAccessor {
    @Accessor("STRUCTURE_TO_GENERATION_STEP")
    static Map<StructureFeature<?>, GenerationStep.Feature> getStructureToGenerationStep() {
        throw new RuntimeException("StructureFeatureAccessor mixin was not mixed in properly!");
    }
}
