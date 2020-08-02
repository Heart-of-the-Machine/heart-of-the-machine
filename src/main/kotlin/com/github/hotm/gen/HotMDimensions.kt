package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.HotMLog
import com.github.hotm.mixinapi.DimensionAdditions
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.block.Blocks
import net.minecraft.block.pattern.BlockPattern
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.BlockTags
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType
import net.minecraft.world.gen.chunk.*
import java.util.*

/**
 * Initializes and registers dimension functionality.
 */
object HotMDimensions {
    private var registered = false

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
    val NECTERE_TYPE = DimensionAdditions.createDimensionType(
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
        DimensionAdditions.createChunkGeneratorType(
            StructuresConfig(false),
            NoiseConfig(
                150,
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
            false,
            Optional.of(preset)
        )
    }

    /**
     * Calls this Registers this mods dimensions.
     *
     * Actual registration happens in a separate method that is protected from being called twice so that servers, which
     * query dimensions before mods are loaded, can call this register method when loading.
     */
    fun register() {
        if (!registered) {
            registered = true
            registerImpl()
        }
    }

    /**
     * Registers the world generator for the Nectere dimension.
     */
    private fun registerImpl() {
        Registry.register(
            Registry.CHUNK_GENERATOR,
            Identifier(HotMConstants.MOD_ID, "nectere"),
            NectereChunkGenerator.CODEC
        )

        DimensionAdditions.addDimension(
            NECTERE_OPTIONS_KEY,
            NECTERE_TYPE_KEY,
            NECTERE_TYPE
        ) { seed -> createNectereGenerator(seed) }

        DimensionAdditions.setSaveDir(NECTERE_KEY, "DIM-nectere")

        FabricDimensions.registerDefaultPlacer(
            NECTERE_KEY
        ) { oldEntity, destination, _, _, _ -> findTeleportationDestination(oldEntity, destination) }
    }

    /**
     * Constructs the chunk generator used for the Nectere dimension.
     */
    private fun createNectereGenerator(seed: Long): NectereChunkGenerator {
        return NectereChunkGenerator(
            FixedBiomeSource(HotMBiomes.THINKING_FOREST),
            seed,
            NECTERE_CHUNK_GENERATOR_TYPE_PRESET.chunkGeneratorType
        )
    }

    /**
     * Performs a teleportation between the Overworld and the Nectere.
     */
    fun performNectereTeleportation(entity: Entity, world: World) {
        world.server?.let { server ->
            if (world.registryKey == NECTERE_KEY) {
                FabricDimensions.teleport(
                    entity,
                    server.getWorld(World.OVERWORLD)
                ) { oldEntity, destination, _, _, _ -> findTeleportationDestination(oldEntity, destination) }
            } else {
                val nectereWorld = server.getWorld(NECTERE_KEY)

                if (nectereWorld == null) {
                    if (entity is PlayerEntity) {
                        entity.sendMessage(
                            TranslatableText(
                                "error.chat.missing_nectere",
                                LiteralText(DimensionAdditions.FORCE_DIMENSION_FLAG).setStyle(
                                    Style.EMPTY.withItalic(true).withColor(TextColor.fromRgb(0x0000FF))
                                )
                            ).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))), false
                        )
                    }

                    HotMLog.log.warn("An entity is attempting to travel through a Nectere portal, but the Nectere dimension does not exist in this world!")
                    HotMLog.log.warn("In order to create the Nectere dimension, place a file called 'force-hotm' in the same directory as the server's server.jar file and restart the server.")
                } else {
                    entity.changeDimension(nectereWorld)
                }
            }
        }
    }

    /**
     * Finds the teleportation destination block.
     */
    private fun findTeleportationDestination(oldEntity: Entity, destination: ServerWorld): BlockPattern.TeleportTarget {
        // TODO implement proper teleportation logic

        // load chunk so heightmap loading works properly
        destination.getChunk(oldEntity.blockPos)

        val position = destination.getTopPosition(Heightmap.Type.WORLD_SURFACE, oldEntity.blockPos)
        return BlockPattern.TeleportTarget(Vec3d.of(position).add(0.5, 0.5, 0.5), Vec3d.ZERO, oldEntity.yaw.toInt())
    }
}
