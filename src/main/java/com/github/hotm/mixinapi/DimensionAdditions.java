package com.github.hotm.mixinapi;

import com.github.hotm.HotMLog;
import com.github.hotm.gen.HotMDimensions;
import com.github.hotm.mixin.*;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
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
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to register a new dimension with the dimension mixin.
 */
public class DimensionAdditions {
    private static final List<DimensionAddition> ADDITIONS = new ArrayList<>();
    private static final Map<RegistryKey<DimensionOptions>, DimensionAddition> DIMENSION_KEYS = new HashMap<>();
    private static final Map<RegistryKey<World>, EntityPlacer> DEFAULT_PLACERS = new HashMap<>();
    private static final ThreadLocal<EntityPlacer> CURRENT_PLACER = new ThreadLocal<>();
    private static boolean schemaInjected = false;

    public static ChunkGeneratorSettings createChunkGeneratorSettings(StructuresConfig structuresConfig,
                                                                      GenerationShapeConfig generationShapeConfig,
                                                                      BlockState defaultBlock, BlockState defaultFluid,
                                                                      int bedrockCeilingY, int bedrockFloorY,
                                                                      int seaLevel,
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
                                                    boolean ultrawarm, boolean natural, double coordinateScale,
                                                    boolean hasEnderDragonFight, boolean piglinSafe, boolean bedWorks,
                                                    boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight,
                                                    BiomeAccessType biomeAccessType, Identifier infiniburn,
                                                    Identifier skyProperties, float ambientLight) {
        return DimensionTypeInvoker
                .create(fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, coordinateScale, hasEnderDragonFight,
                        piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, biomeAccessType, infiniburn,
                        skyProperties, ambientLight);
    }

    public static void addDimension(DimensionAddition addition) {
        DIMENSION_KEYS.put(addition.getOptionsRegistryKey(), addition);
        ADDITIONS.add(addition);
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
        // Make sure dimensions are registered in time.
        HotMDimensions.INSTANCE.register();

        HotMLog.INSTANCE.getLog().info("HotM Adding Dimensions to " + message + ":");
        for (DimensionAddition addition : ADDITIONS) {
            if (!optionsRegistry.getIds().contains(addition.getOptionsRegistryKey().getValue())) {
                optionsRegistry.add(addition.getOptionsRegistryKey(),
                        new DimensionOptions(() -> dimensionTypes.getOrThrow(addition.getTypeRegistryKey()),
                                addition.getChunkGeneratorSupplier()
                                        .getChunkGenerator(biomes, generatorSettings, seed)), Lifecycle.stable());
                HotMLog.INSTANCE.getLog().info("    " + addition.getOptionsRegistryKey());
            } else {
                HotMLog.INSTANCE.getLog().info("    " + addition.getOptionsRegistryKey() + " : ALREADY REGISTERED");
            }
        }
    }

    public static void setupDimensionTypes(MutableRegistry<DimensionType> dimensionTypes) {
        for (DimensionAddition addition : ADDITIONS) {
            dimensionTypes.add(addition.getTypeRegistryKey(), addition.getDimensionType(), Lifecycle.stable());
        }
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

    private static boolean replaceTypeTemplate(TypeTemplate base, String name,
                                               Function<TypeTemplate, TypeTemplate> replacer) {
        if (base instanceof ProductAccessor) {
            if (replaceTypeTemplate(((ProductAccessor) base).getF(), name, replacer)) {
                return true;
            }
            return replaceTypeTemplate(((ProductAccessor) base).getG(), name, replacer);
        } else if (base instanceof TagAccessor) {
            if (Objects.equals(((TagAccessor) base).getName(), name)) {
                ((TagAccessor) base).setElement(replacer.apply(((TagAccessor) base).getElement()));
                return true;
            }
            return replaceTypeTemplate(((TagAccessor) base).getElement(), name, replacer);
        } else if (base instanceof CompoundListAccessor) {
            return replaceTypeTemplate(((CompoundListAccessor) base).getElement(), name, replacer);
        }

        return false;
    }

    public static void injectChunkGeneratorSchema(TypeTemplate tt, Schema schema) {
        if (!replaceTypeTemplate(tt, "generator", generator -> {
            if (generator instanceof TaggedChoiceAccessor) {
                TaggedChoiceAccessor<String> tc = (TaggedChoiceAccessor<String>) generator;

                ImmutableMap.Builder<String, TypeTemplate> builder =
                        ImmutableMap.<String, TypeTemplate>builder().putAll(tc.getTemplates());

                HotMDimensions.INSTANCE.injectChunkGeneratorSchema(schema, builder);

                schemaInjected = true;

                return new TaggedChoice<>(tc.getName(), tc.getKeyType(), builder.build());
            } else {
                HotMLog.INSTANCE.getLog().error("Unable to find \"generator\" TypeTemplate of correct type (type: " +
                        generator.getClass() +
                        "). This means that worlds will not migrate between minecraft versions correctly!");
            }
            return generator;
        })) {
            HotMLog.INSTANCE.getLog()
                    .error("Unable to find \"generator\" TypeTemplate. This means that worlds will not migrate between minecraft versions correctly!");
        }
    }

    public static TaggedChoice.TaggedChoiceType<String> injectChunkGeneratorTypes(
            TaggedChoice.TaggedChoiceType<String> original, Schema schema) {
        if (!schemaInjected) {
            HotMLog.INSTANCE.getLog().error("HotM schema was not injected, skipping data-fixer type injection...");
            return original;
        }

        ImmutableMap.Builder<String, Type<?>> builder =
                ImmutableMap.<String, Type<?>>builder().putAll(original.types());

        HotMDimensions.INSTANCE.injectChunkGeneratorTypes(schema, builder);

        return new TaggedChoice.TaggedChoiceType<>("type", DSL.string(), builder.build());
    }
}
