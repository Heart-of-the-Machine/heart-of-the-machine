package com.github.hotm.mixinopts;

import com.github.hotm.gen.HotMDimensions;
import com.github.hotm.mixin.ChunkGeneratorTypeInvoker;
import com.github.hotm.mixin.DimensionTypeInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

import java.io.File;
import java.util.*;

/**
 * Used to register a new dimension with the dimension mixin.
 */
public class DimensionAdditions {
    public static final String FORCE_DIMENSION_FLAG = "force-hotm";

    private static final List<DimensionAddition> ADDITIONS = new ArrayList<>();
    private static final Map<RegistryKey<DimensionOptions>, DimensionAddition> DIMENSION_KEYS = new HashMap<>();
    private static final Map<RegistryKey<World>, String> SAVE_DIRS = new HashMap<>();

    public static ChunkGeneratorType createChunkGeneratorType(StructuresConfig structures, NoiseConfig noise,
                                                              BlockState defaultBlock, BlockState defaultFluid,
                                                              int bedrockRoofPosition, int bedrockFloorPosition,
                                                              int seaLevel, boolean disableMobGeneration) {
        return ChunkGeneratorTypeInvoker
                .create(structures, noise, defaultBlock, defaultFluid, bedrockRoofPosition, bedrockFloorPosition,
                        seaLevel, disableMobGeneration);
    }

    public static ChunkGeneratorType createChunkGeneratorType(StructuresConfig structures, NoiseConfig noise,
                                                              BlockState defaultBlock, BlockState defaultFluid,
                                                              int bedrockRoofPosition, int bedrockFloorPosition,
                                                              int seaLevel, boolean disableMobGeneration,
                                                              Optional<ChunkGeneratorType.Preset> preset) {
        return ChunkGeneratorTypeInvoker
                .create(structures, noise, defaultBlock, defaultFluid, bedrockRoofPosition, bedrockFloorPosition,
                        seaLevel, disableMobGeneration, preset);
    }

    public static DimensionType createDimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling,
                                                    boolean ultrawarm, boolean natural, boolean shrunk,
                                                    boolean hasEnderDragonFight, boolean piglinSafe, boolean bedWorks,
                                                    boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight,
                                                    BiomeAccessType biomeAccessType, Identifier infiniburn,
                                                    float ambientLight) {
        return DimensionTypeInvoker
                .create(fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, shrunk, hasEnderDragonFight, piglinSafe,
                        bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, biomeAccessType, infiniburn,
                        ambientLight);
    }

    public static void addDimension(DimensionAddition addition) {
        DIMENSION_KEYS.put(addition.getOptionsRegistryKey(), addition);
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
        // Make sure dimensions are registered in time.
        HotMDimensions.INSTANCE.register();

        System.out.println("HotM Adding Dimensions:");
        for (DimensionAddition addition : ADDITIONS) {
            if (!optionsRegistry.containsId(addition.getOptionsRegistryKey().getValue())) {
                optionsRegistry.add(addition.getOptionsRegistryKey(), new DimensionOptions(addition::getDimensionType,
                        addition.getChunkGeneratorSupplier().getChunkGenerator(seed)));
                optionsRegistry.markLoaded(addition.getOptionsRegistryKey());
                System.out.println("    " + addition.getOptionsRegistryKey());
            } else {
                System.out.println("    " + addition.getOptionsRegistryKey() + " : ALREADY REGISTERED");
            }
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

    public static boolean checkAndRemoveDimensions(long seed,
                                                   List<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> list) {
        Iterator<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> it = list.iterator();

        while (it.hasNext()) {
            Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry = it.next();
            RegistryKey<DimensionOptions> key = entry.getKey();
            DimensionOptions options = entry.getValue();

            if (DIMENSION_KEYS.containsKey(key)) {
                DimensionAddition addition = DIMENSION_KEYS.get(key);

                // do some basic checks to make sure the dimension hasn't been customized
                if (options.getDimensionType() != addition.getDimensionType()) {
                    return false;
                }

                ChunkGenerator generator = addition.getChunkGeneratorSupplier().getChunkGenerator(seed);
                if (!generator.getClass().isAssignableFrom(options.getChunkGenerator().getClass())) {
                    return false;
                }

                // not quite sure how to verify generator settings

                it.remove();
            }
        }

        return true;
    }

    public static boolean shouldForceDimensions() {
        File flag = new File(FORCE_DIMENSION_FLAG);
        if (flag.exists()) {
            if (!flag.delete()) {
                System.err.println("Error deleting force dimension flag file: " + flag.getAbsolutePath());
            }
            return true;
        }
        return false;
    }
}
