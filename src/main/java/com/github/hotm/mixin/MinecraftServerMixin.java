package com.github.hotm.mixin;

import com.github.hotm.mixinapi.DimensionAdditions;
import com.github.hotm.mixinapi.MutableMinecraftServer;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    protected RegistryTracker.Modifiable dimensionTracker;

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
    public void hotm_addDimension(RegistryKey<DimensionOptions> optionsKey, DimensionOptions dimensionOptions) {
        // transmute this into a MinecraftServer
        Object selfObj = this;
        MinecraftServer self = (MinecraftServer) selfObj;

        GeneratorOptions generatorOptions = saveProperties.getGeneratorOptions();
        WorldBorder worldBorder =
                Objects.requireNonNull(self.getWorld(World.OVERWORLD), "Missing Overworld?!").getWorldBorder();
        WorldGenerationProgressListener listener = worldGenerationProgressListenerFactory.create(11);

        RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, optionsKey.getValue());
        DimensionType dimensionType = dimensionOptions.getDimensionType();
        RegistryKey<DimensionType> typeKey =
                dimensionTracker.getDimensionTypeRegistry().getKey(dimensionType).orElseThrow(() -> {
                    throw new IllegalStateException("Attempting to add unregistered dimension type: " + dimensionType);
                });
        ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();
        UnmodifiableLevelProperties levelProperties =
                new UnmodifiableLevelProperties(saveProperties, saveProperties.getMainWorldProperties());
        ServerWorld world =
                new ServerWorld(self, workerExecutor, session, levelProperties, worldKey, typeKey, dimensionType,
                        listener, chunkGenerator, generatorOptions.isDebugWorld(),
                        BiomeAccess.hashSeed(generatorOptions.getSeed()), ImmutableList.of(), false);
        worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(world.getWorldBorder()));
        worlds.put(worldKey, world);
    }
}
