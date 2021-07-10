package com.github.hotm.mixinapi;

import com.github.hotm.world.gen.feature.HotMConfiguredFeatures;
import com.github.hotm.world.gen.feature.HotMStructureFeatures;
import com.github.hotm.mixin.StructureFeatureAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * API to handle registering new structures and features.
 */
public class FeatureAdditions {
    /**
     * This is called every time a GenerationSettings.Builder.build() function is called to add features to the biome
     * being built. This means that every biome will get the features that this method adds.
     * <p>
     * This calls Heart of the Machine's HotMBiomeFeatures addUbiquitousFeatures to add Nectere portals to every biome.
     * Per-biome Nectere portal disabling is handled in the Feature's Decorator.
     *
     * @param settings a reference to the builder of the biome generation settings being built.
     */
    public static void addUbiquitousGenerationSettings(GenerationSettings.Builder settings) {
        HotMConfiguredFeatures.INSTANCE.addUbiquitousFeatures(settings);
    }
}
