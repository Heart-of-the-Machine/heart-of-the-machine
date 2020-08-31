package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAddition;
import com.github.hotm.mixinapi.MutableMinecraftServer;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Mixin to handle forcing the generation of the nectere dimension.
 * <p>
 * This is only useful for retroactively generating the nectere dimension in a pre-existing world.
 */
@SuppressWarnings("unused")
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MutableMinecraftServer {
    @Shadow
    @Final
    protected DynamicRegistryManager.Impl registryManager;

    @Shadow
    @Final
    protected SaveProperties saveProperties;

    @Shadow
    @Final
    protected LevelStorage.Session session;

    @Shadow
    @Final
    private Executor workerExecutor;

    @Shadow
    @Final
    private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;

    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Override
    public long hotm_getSeed() {
        return saveProperties.getGeneratorOptions().getSeed();
    }

    @Override
    public void hotm_addDimension(RegistryKey<DimensionOptions> optionsKey, DimensionAddition dimensionAddition) {
        // transmute this into a MinecraftServer
        Object selfObj = this;
        MinecraftServer self = (MinecraftServer) selfObj;

        GeneratorOptions generatorOptions = saveProperties.getGeneratorOptions();
        WorldBorder worldBorder =
                Objects.requireNonNull(self.getWorld(World.OVERWORLD), "Missing Overworld?!").getWorldBorder();
        WorldGenerationProgressListener listener = worldGenerationProgressListenerFactory.create(11);

        Registry<DimensionType> dimensionTypes = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        Registry<Biome> biomes = registryManager.get(Registry.BIOME_KEY);
        Registry<ChunkGeneratorSettings> generatorSettings = registryManager.get(Registry.NOISE_SETTINGS_WORLDGEN);

        RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, optionsKey.getValue());
        DimensionType dimensionType = dimensionTypes.getOrThrow(dimensionAddition.getTypeRegistryKey());
        ChunkGenerator chunkGenerator = dimensionAddition.getChunkGeneratorSupplier().getChunkGenerator(biomes, generatorSettings, generatorOptions.getSeed());
        UnmodifiableLevelProperties levelProperties =
                new UnmodifiableLevelProperties(saveProperties, saveProperties.getMainWorldProperties());
        ServerWorld world =
                new ServerWorld(self, workerExecutor, session, levelProperties, worldKey, dimensionType,
                        listener, chunkGenerator, generatorOptions.isDebugWorld(),
                        BiomeAccess.hashSeed(generatorOptions.getSeed()), ImmutableList.of(), false);
        worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(world.getWorldBorder()));
        worlds.put(worldKey, world);
    }
}
