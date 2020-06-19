package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.mixin.ChunkGeneratorTypeInvoker
import com.github.hotm.mixin.DimensionTypeInvoker
import com.github.hotm.mixinopts.DimensionAdditions
import net.minecraft.block.Blocks
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType
import net.minecraft.world.gen.chunk.*
import java.util.*

/**
 * Initializes and registers dimension functionality.
 */
object HotMDimensions {
    /**
     * Key used to reference the Nectere dimension.
     */
    val NECTERE_KEY: RegistryKey<World> =
        RegistryKey.of(Registry.DIMENSION, Identifier(HotMConstants.MOD_ID, "nectere"))

    /**
     * Key used to reference the Nectere dimension options.
     */
    val NECTERE_OPTIONS_KEY = RegistryKey.of(Registry.DIMENSION_OPTIONS, Identifier(HotMConstants.MOD_ID, "nectere"))

    /**
     * Dimension options that describe the Nectere dimension.
     */
    val NECTERE_TYPE = DimensionTypeInvoker.create(
        OptionalLong.empty(),
        true,
        false,
        false,
        true,
        false,
        false,
        false,
        false,
        false,
        false,
        256,
        HorizontalVoronoiBiomeAccessType.INSTANCE,
        BlockTags.INFINIBURN_OVERWORLD.id,
        0.1f
    )

    /**
     * Key used to reference the Nectere dimension type.
     */
    val NECTERE_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Identifier(HotMConstants.MOD_ID, "nectere"))

    /**
     * ChunkGeneratorType preset for the Nectere dimension.
     */
    val NECTERE_CHUNK_GENERATOR_TYPE_PRESET = ChunkGeneratorType.Preset("${HotMConstants.MOD_ID}:nectere") { preset ->
        ChunkGeneratorTypeInvoker.create(
            StructuresConfig(false),
            NoiseConfig(
                256,
                NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0),
                SlideConfig(-10, 3, 0),
                SlideConfig(-30, 0, 0),
                1,
                2,
                1.0,
                -0.46875,
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
            false,
            Optional.of(preset)
        )
    }

    /**
     * Registers the world generator for the Nectere dimension.
     */
    fun register() {
        // Unused chunk generator
//        Registry.register(
//            Registry.CHUNK_GENERATOR,
//            Identifier(HotMConstants.MOD_ID, "nectere"),
//            NectereChunkGenerator.CODEC
//        )

        DimensionAdditions.addDimension(
            NECTERE_OPTIONS_KEY,
            NECTERE_TYPE_KEY,
            NECTERE_TYPE
        ) { seed -> createNectereGenerator(seed) }

        DimensionAdditions.setSaveDir(NECTERE_KEY, "DIM-nectere")
    }

    /**
     * Constructs the chunk generator used for the Nectere dimension.
     */
    private fun createNectereGenerator(seed: Long): SurfaceChunkGenerator {
        return SurfaceChunkGenerator(
            FixedBiomeSource(HotMBiomes.THINKING_FOREST),
            seed,
            NECTERE_CHUNK_GENERATOR_TYPE_PRESET.chunkGeneratorType
        )
    }
}
