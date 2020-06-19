package com.github.hotm.mixinopts;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.util.*;

/**
 * Used to register a new dimension with the dimension mixin.
 */
public class DimensionAdditions {
    private static final List<DimensionAddition> ADDITIONS = new ArrayList<>();
    private static final Set<RegistryKey<DimensionOptions>> DIMENSION_KEYS = new HashSet<>();
    private static final Map<RegistryKey<World>, String> SAVE_DIRS = new HashMap<>();

    public static void addDimension(DimensionAddition addition) {
        DIMENSION_KEYS.add(addition.getOptionsRegistryKey());
        ADDITIONS.add(addition);
    }

    public static void addDimension(RegistryKey<DimensionOptions> optionsKey, RegistryKey<DimensionType> typeKey,
                                    DimensionType type, ChunkGeneratorSupplier chunkSupplier) {
        addDimension(new DimensionAddition(optionsKey, typeKey, type, chunkSupplier));
    }

    public static void setSaveDir(RegistryKey<World> key, String saveDir) {
        SAVE_DIRS.put(key, saveDir);
    }

    public static void setupDimensionOptions(long seed, SimpleRegistry<DimensionOptions> optionsRegistry) {
        for (DimensionAddition addition : ADDITIONS) {
            optionsRegistry.add(addition.getOptionsRegistryKey(), new DimensionOptions(addition::getDimensionType,
                    addition.getChunkGeneratorSupplier().getChunkGenerator(seed)));
            optionsRegistry.markLoaded(addition.getOptionsRegistryKey());
        }
    }

    public static void setupDimensionTypes(RegistryTracker.Modifiable modifiable) {
        for (DimensionAddition addition : ADDITIONS) {
            modifiable.addDimensionType(addition.getTypeRegistryKey(), addition.getDimensionType());
        }
    }

    public static boolean containsSaveDir(RegistryKey<World> key) {
        return SAVE_DIRS.containsKey(key);
    }

    public static File getSaveDir(RegistryKey<World> key, File root) {
        return new File(root, SAVE_DIRS.get(key));
    }

    public static boolean containsOnlyValidKeys(SimpleRegistry<DimensionOptions> registry) {
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> e : registry.getEntries()) {
            RegistryKey<DimensionOptions> key = e.getKey();
            if (key != DimensionOptions.OVERWORLD && key != DimensionOptions.NETHER && key != DimensionOptions.END &&
                    !DIMENSION_KEYS.contains(key)) {
                return false;
            }
        }
        return true;
    }
}
