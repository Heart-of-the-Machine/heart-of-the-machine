package com.github.hotm.gen

import com.github.hotm.HotMConstants
import com.github.hotm.HotMDimensionType
import com.github.hotm.mixinopts.DimensionAdditions
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType
import net.minecraft.world.gen.chunk.ChunkGeneratorType
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator
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
    val NECTERE_TYPE = HotMDimensionType(
        fixedTime = OptionalLong.empty(),
        hasSkyLight = true,
        hasCeiling = false,
        ultrawarm = false,
        natural = true,
        shrunk = false,
        hasEnderDragonFight = false,
        piglinSafe = false,
        bedWorks = false,
        respawnAnchorWorks = false,
        hasRaids = false,
        logicalHeight = 256,
        biomeAccessType = HorizontalVoronoiBiomeAccessType.INSTANCE,
        infiniburn = BlockTags.INFINIBURN_OVERWORLD.id,
        ambientLight = 0.1f
    )

    /**
     *
     */
    val NECTERE_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Identifier(HotMConstants.MOD_ID, "nectere"))

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
            // TODO fix world base block
            ChunkGeneratorType.Preset.OVERWORLD.chunkGeneratorType
        )
    }
}