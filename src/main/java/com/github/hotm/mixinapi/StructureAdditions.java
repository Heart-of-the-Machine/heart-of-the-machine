package com.github.hotm.mixinapi;

import com.github.hotm.gen.feature.HotMStructureFeatures;
import com.github.hotm.mixin.StructureFeatureAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * API to handle registering new structures.
 */
public class StructureAdditions {
    public static <F extends StructureFeature<?>> F register(String name, F structure, GenerationStep.Feature step) {
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
        config.put(HotMStructureFeatures.INSTANCE.getNECTERE_PORTAL(), new StructureConfig(32, 8, 103873));
        return config.build();
    }
}
