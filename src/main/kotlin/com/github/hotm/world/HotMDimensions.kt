package com.github.hotm.world

import com.github.hotm.HotMConstants
import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.mixinapi.ChunkGeneratorSettingsAccess
import com.github.hotm.mixinapi.DimensionAdditions
import com.github.hotm.mixinapi.MultiNoiseBiomeSourceAccess
import com.github.hotm.world.biome.HotMBiomes
import com.github.hotm.world.gen.chunk.NectereChunkGenerator
import com.google.common.collect.ImmutableList
import com.mojang.datafixers.util.Pair
import net.minecraft.block.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.BlockTags
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType
import net.minecraft.world.biome.source.MultiNoiseBiomeSource
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.chunk.*
import java.util.*
import java.util.function.Supplier

/**
 * Initializes and registers dimension functionality.
 */
object HotMDimensions {

    /**
     * Key used to reference the Nectere dimension.
     */
    val NECTERE_KEY: RegistryKey<World> by lazy {
        RegistryKey.of(Registry.WORLD_KEY, HotMConstants.identifier("nectere"))
    }

    /**
     * Key used to reference the Nectere dimension options.
     */
    val NECTERE_OPTIONS_KEY: RegistryKey<DimensionOptions> by lazy {
        RegistryKey.of(Registry.DIMENSION_KEY, HotMConstants.identifier("nectere"))
    }

    /**
     * Key used to reference the Nectere dimension type.
     */
    val NECTERE_TYPE_KEY by lazy {
        RegistryKey.of(Registry.DIMENSION_TYPE_KEY, HotMConstants.identifier("nectere"))
    }

    /**
     * The registry key for the Nectere chunk generator settings.
     */
    val NECTERE_CHUNK_GENERATOR_SETTINGS_KEY by lazy {
        RegistryKey.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, HotMConstants.identifier("nectere"))
    }

    /**
     * Dimension options that describe the Nectere dimension.
     */
    lateinit var NECTERE_TYPE: DimensionType
        private set

    /**
     * ChunkGeneratorType preset for the Nectere dimension.
     */
    lateinit var NECTERE_CHUNK_GENERATOR_SETTINGS_BUILTIN: ChunkGeneratorSettings
        private set

    /**
     * Biome source preset for the Nectere dimension.
     */
    lateinit var NECTERE_BIOME_SOURCE_PRESET: MultiNoiseBiomeSource.Preset
        private set

    /**
     * Registers the world generator for the Nectere dimension.
     */
    fun register() {
        NECTERE_TYPE = DimensionType.create(
            OptionalLong.empty(),
            true,
            false,
            false,
            true,
            1.0,
            false,
            false,
            false,
            false,
            false,
            0,
            256,
            256,
            HorizontalVoronoiBiomeAccessType.INSTANCE,
            BlockTags.INFINIBURN_OVERWORLD.id,
            DimensionType.OVERWORLD_ID,
            0.1f
        )

        NECTERE_CHUNK_GENERATOR_SETTINGS_BUILTIN = Registry.register(
            BuiltinRegistries.CHUNK_GENERATOR_SETTINGS,
            NECTERE_CHUNK_GENERATOR_SETTINGS_KEY.value,
            ChunkGeneratorSettingsAccess.create(
                StructuresConfig(false),
                GenerationShapeConfig.create(
                    0,
                    160,
                    NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 60.0),
                    SlideConfig(-10, 3, 0),
                    SlideConfig(50, 4, -1),
                    1,
                    2,
                    -0.02,
                    -0.02,
                    true,
                    true,
                    false,
                    false
                ),
                HotMBlocks.THINKING_STONE.defaultState,
                Blocks.WATER.defaultState,
                -10,
                0,
                16,
                0,
                false,
                false,
                false,
                false,
                false,
                true
            )
        )

        NECTERE_BIOME_SOURCE_PRESET =
            MultiNoiseBiomeSource.Preset(HotMConstants.identifier("nectere")) { preset, registry, seed ->
                MultiNoiseBiomeSourceAccess.create(
                    seed,
                    HotMBiomes.biomeNoise().entries.stream()
                        .map<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> { entry ->
                            Pair.of(entry.value, Supplier { registry.getOrThrow(entry.key) })
                        }.collect(ImmutableList.toImmutableList()),
                    Optional.of(Pair.of(registry, preset))
                )
            }

        Registry.register(
            Registry.CHUNK_GENERATOR,
            HotMConstants.identifier("nectere"),
            NectereChunkGenerator.CODEC
        )

        DimensionAdditions.addDimension(
            NECTERE_OPTIONS_KEY,
            NECTERE_TYPE_KEY,
            NECTERE_TYPE
        ) { biomes, generatorSettings, seed -> createNectereGenerator(biomes, generatorSettings, seed) }
    }

    /**
     * Constructs the chunk generator used for the Nectere dimension.
     */
    private fun createNectereGenerator(
        biomes: Registry<Biome>,
        generatorSettings: Registry<ChunkGeneratorSettings>,
        seed: Long
    ): NectereChunkGenerator {
        return NectereChunkGenerator(
            NECTERE_BIOME_SOURCE_PRESET.getBiomeSource(biomes, seed),
            seed,
            { generatorSettings.getOrThrow(NECTERE_CHUNK_GENERATOR_SETTINGS_KEY) },
            biomes
        )
    }

    /**
     * Gets the Nectere dimension from the server, forcibly adding it if it does not exist already.
     */
    fun getNectereWorld(server: MinecraftServer): ServerWorld {
        return server.getWorld(NECTERE_KEY) ?: throw IllegalStateException("Nectere dimension was never added!")
    }
}