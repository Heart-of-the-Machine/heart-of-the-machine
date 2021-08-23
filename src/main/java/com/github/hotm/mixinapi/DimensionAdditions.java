package com.github.hotm.mixinapi;

import com.github.hotm.misc.HotMLog;
import com.mojang.serialization.Lifecycle;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Used to register a new dimension with the dimension mixin.
 */
public class DimensionAdditions {
    private static final List<DimensionAddition> ADDITIONS = new ArrayList<>();
    private static final Map<RegistryKey<DimensionOptions>, DimensionAddition> DIMENSION_KEYS = new HashMap<>();
    private static final List<MutableRegistry<DimensionType>> REGISTRIES = new ArrayList<>();
    private static final Map<RegistryKey<World>, EntityPlacer> DEFAULT_PLACERS = new HashMap<>();
    private static final ThreadLocal<EntityPlacer> CURRENT_PLACER = new ThreadLocal<>();

    public static void addDimension(DimensionAddition addition) {
        HotMLog.getLog().info("HotM Registering dimension: " + addition.getTypeRegistryKey().getValue());

        DIMENSION_KEYS.put(addition.getOptionsRegistryKey(), addition);
        ADDITIONS.add(addition);

        // Add this dimension type to all preexisting DynamicRegistryManagers.
        for (MutableRegistry<DimensionType> registry : REGISTRIES) {
            registry.add(addition.getTypeRegistryKey(), addition.getDimensionType(), Lifecycle.stable());
        }
    }

    public static void addDimension(RegistryKey<DimensionOptions> optionsKey, RegistryKey<DimensionType> typeKey,
                                    DimensionType type, ChunkGeneratorSupplier chunkSupplier) {
        addDimension(new DimensionAddition(optionsKey, typeKey, type, chunkSupplier));
    }

    public static void setupDimensionOptions(
            Registry<DimensionType> dimensionTypes,
            Registry<Biome> biomes,
            Registry<ChunkGeneratorSettings> generatorSettings,
            long seed,
            SimpleRegistry<DimensionOptions> optionsRegistry,
            String message) {

        HotMLog.getLog().info("HotM Adding dimensions to " + message + ":");
        for (DimensionAddition addition : ADDITIONS) {
            if (!optionsRegistry.getIds().contains(addition.getOptionsRegistryKey().getValue())) {
                optionsRegistry.add(addition.getOptionsRegistryKey(),
                        new DimensionOptions(() -> dimensionTypes.getOrThrow(addition.getTypeRegistryKey()),
                                addition.getChunkGeneratorSupplier()
                                        .getChunkGenerator(biomes, generatorSettings, seed)), Lifecycle.stable());
                HotMLog.getLog().info("    " + addition.getOptionsRegistryKey());
            } else {
                HotMLog.getLog().info("    " + addition.getOptionsRegistryKey() + " : ALREADY REGISTERED");
            }
        }
    }

    public static void setupDimensionTypes(MutableRegistry<DimensionType> dimensionTypes) {
        HotMLog.getLog().info("HotM Performing dimension registration and registry instance capture.");

        REGISTRIES.add(dimensionTypes);
        for (DimensionAddition addition : ADDITIONS) {
            dimensionTypes.add(addition.getTypeRegistryKey(), addition.getDimensionType(), Lifecycle.stable());
        }
    }

    public static boolean checkAndRemoveDimensions(long seed,
                                                   List<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list) {
        HotMLog.getLog().info("HotM Validating dimension options...");

        Iterator<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> it = list.iterator();

        while (it.hasNext()) {
            Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry = it.next();
            RegistryKey<DimensionOptions> key = entry.getKey();
            DimensionOptions options = entry.getValue();

            if (DIMENSION_KEYS.containsKey(key)) {
                DimensionAddition addition = DIMENSION_KEYS.get(key);

                // do some basic checks to make sure the dimension hasn't been customized
                if (options.getDimensionType() != addition.getDimensionType()) {
                    HotMLog.getLog().info("    Modified: " + addition.getTypeRegistryKey().getValue());
                    return false;
                }

                // not quite sure how to verify generator settings
                // TODO: Add more rigorous checking

                it.remove();

                HotMLog.getLog().info("    " + addition.getTypeRegistryKey().getValue() + " Validated.");
            }
        }

        return true;
    }

    public static void registerDefaultPlacer(RegistryKey<World> key, EntityPlacer placer) {
        DEFAULT_PLACERS.put(key, placer);
    }

    @Nullable
    public static Entity teleport(Entity entity, ServerWorld destination, EntityPlacer placer) {
        // Kind of a gross hack, but I don't see any other way to get the custom entity placer into the EntityMixin.
        // Besides, I'm pretty sure FabricDimensions used a ThreadLocal for this as well.
        CURRENT_PLACER.set(placer);
        try {
            return entity.moveToWorld(destination);
        } finally {
            CURRENT_PLACER.set(null);
        }
    }

    public static boolean shouldUseCustomPlacer(ServerWorld destination) {
        return CURRENT_PLACER.get() != null || DEFAULT_PLACERS.containsKey(destination.getRegistryKey());
    }

    public static TeleportTarget useCustomPlacer(Entity entity, ServerWorld destination) {
        EntityPlacer placer = CURRENT_PLACER.get();
        if (placer == null) {
            placer = DEFAULT_PLACERS.get(destination.getRegistryKey());
        }

        return placer.placeEntity(entity, destination);
    }
}
