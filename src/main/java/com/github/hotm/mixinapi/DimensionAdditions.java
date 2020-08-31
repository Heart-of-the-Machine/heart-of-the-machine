package com.github.hotm.mixinapi;

import com.github.hotm.gen.HotMDimensions;
import com.github.hotm.mixin.ChunkGeneratorSettingsInvoker;
import com.github.hotm.mixin.DimensionTypeInvoker;
import com.github.hotm.mixin.MultiNoiseBiomeSourceInvoker;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Supplier;

/**
 * Used to register a new dimension with the dimension mixin.
 */
public class DimensionAdditions {
    private static final List<DimensionAddition> ADDITIONS = new ArrayList<>();
    private static final Map<RegistryKey<DimensionOptions>, DimensionAddition> DIMENSION_KEYS = new HashMap<>();
    private static final Map<RegistryKey<World>, String> SAVE_DIRS = new HashMap<>();
    private static final Map<RegistryKey<World>, EntityPlacer> DEFAULT_PLACERS = new HashMap<>();
    private static final ThreadLocal<EntityPlacer> CURRENT_PLACER = new ThreadLocal<>();

    public static ChunkGeneratorSettings createChunkGeneratorSettings(StructuresConfig structuresConfig,
                                                                      GenerationShapeConfig generationShapeConfig,
                                                                      BlockState defaultBlock, BlockState defaultFluid,
                                                                      int bedrockCeilingY, int bedrockFloorY, int seaLevel,
                                                                      boolean mobGenerationDisabled) {
        return ChunkGeneratorSettingsInvoker
                .create(structuresConfig, generationShapeConfig, defaultBlock, defaultFluid, bedrockCeilingY,
                        bedrockFloorY, seaLevel, mobGenerationDisabled);
    }

    public static MultiNoiseBiomeSource createMultiNoiseBiomeSource(long seed,
                                                                    List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list,
                                                                    Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional) {
        return MultiNoiseBiomeSourceInvoker.create(seed, list, optional);
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

    public static void setupDimensionOptions(
            Registry<DimensionType> dimensionTypes,
            Registry<Biome> biomes,
            Registry<ChunkGeneratorSettings> generatorSettings,
            long seed,
            SimpleRegistry<DimensionOptions> optionsRegistry) {
        // Make sure dimensions are registered in time.
        HotMDimensions.INSTANCE.register();

        System.out.println("HotM Adding Dimensions:");
        for (DimensionAddition addition : ADDITIONS) {
            if (!optionsRegistry.containsId(addition.getOptionsRegistryKey().getValue())) {
                optionsRegistry.add(addition.getOptionsRegistryKey(),
                        new DimensionOptions(() -> dimensionTypes.getOrThrow(addition.getTypeRegistryKey()),
                                addition.getChunkGeneratorSupplier()
                                        .getChunkGenerator(biomes, generatorSettings, seed)), Lifecycle.stable());
                System.out.println("    " + addition.getOptionsRegistryKey());
            } else {
                System.out.println("    " + addition.getOptionsRegistryKey() + " : ALREADY REGISTERED");
            }
        }
    }

    public static void setupDimensionTypes(MutableRegistry<DimensionType> dimensionTypes) {
        for (DimensionAddition addition : ADDITIONS) {
            dimensionTypes.add(addition.getTypeRegistryKey(), addition.getDimensionType(), Lifecycle.stable());
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

                // not quite sure how to verify generator settings
                // TODO: Add more rigorous checking

                it.remove();
            }
        }

        return true;
    }

    public static void addDimensionToServer(MutableMinecraftServer server, RegistryKey<DimensionOptions> optionsKey) {
        DimensionAddition addition = DIMENSION_KEYS.get(optionsKey);
        server.hotm_addDimension(optionsKey, addition);
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
