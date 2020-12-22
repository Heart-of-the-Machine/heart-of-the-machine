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
    public static <F extends StructureFeature<?>> F registerStructure(String name, F structure,
                                                                      GenerationStep.Feature step) {
        StructureFeature.STRUCTURES.put(name, structure);
        StructureFeatureAccessor.getStructureToGenerationStep().put(structure, step);
        return Registry.register(Registry.STRUCTURE_FEATURE, name, structure);
    }

    /**
     * This is called by the StructuresConfigMixin when StructuresConfig's static initializer is called.
     * <p>
     * When a new StructuresConfig is created, it copies all the values from DEFAULT_STRUCTURES. This unfortunately
     * means that this mod's additions to the DEFAULT_STRUCTURES field must be hard-coded because there is not guarantee
     * that this mod will have initialized by the time the first StructuresConfigs are created.
     *
     * @param defaultConfig the previous value of the DEFAULT_STRUCTURES field.
     * @return the new value of the DEFAULT_STRUCTURES field.
     */
    public static ImmutableMap<StructureFeature<?>, StructureConfig> addDefaultStructureConfigs(
            ImmutableMap<StructureFeature<?>, StructureConfig> defaultConfig) {
        ImmutableMap.Builder<StructureFeature<?>, StructureConfig> config = ImmutableMap.builder();
        config.putAll(defaultConfig);
        HotMStructureFeatures.INSTANCE.addConfigs(config);
        return config.build();
    }

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
