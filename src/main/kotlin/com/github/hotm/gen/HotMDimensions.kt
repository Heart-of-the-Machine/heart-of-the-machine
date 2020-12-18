package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConfig
import com.github.hotm.HotMConstants
import com.github.hotm.HotMLog
import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import com.github.hotm.gen.biome.NectereBiomeData
import com.github.hotm.gen.feature.HotMStructureFeatures
import com.github.hotm.gen.feature.NecterePortalGen
import com.github.hotm.mixin.EntityAccessor
import com.github.hotm.mixin.StructureFeatureAccessor
import com.github.hotm.mixinapi.DimensionAdditions
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.mojang.datafixers.util.Pair
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.floor
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.*
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType
import net.minecraft.world.biome.source.MultiNoiseBiomeSource
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.chunk.*
import net.minecraft.world.gen.feature.FeatureConfig
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

/**
 * Initializes and registers dimension functionality.
 */
object HotMDimensions {
    private var registered = false

    private val NECTERE_PORTAL_BIOMES = HashMultimap.create<RegistryKey<World>, NectereBiomeData>()

    /**
     * Key used to reference the Nectere dimension.
     */
    val NECTERE_KEY: RegistryKey<World> =
        RegistryKey.of(Registry.DIMENSION, HotMConstants.identifier("nectere"))

    /**
     * Key used to reference the Nectere dimension options.
     */
    val NECTERE_OPTIONS_KEY = RegistryKey.of(Registry.DIMENSION_OPTIONS, HotMConstants.identifier("nectere"))

    /**
     * Dimension options that describe the Nectere dimension.
     */
    val NECTERE_TYPE = DimensionAdditions.createDimensionType(
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
        256,
        HorizontalVoronoiBiomeAccessType.INSTANCE,
        BlockTags.INFINIBURN_OVERWORLD.id,
        DimensionType.OVERWORLD_ID,
        0.1f
    )

    /**
     * Key used to reference the Nectere dimension type.
     */
    val NECTERE_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, HotMConstants.identifier("nectere"))

    /**
     * The registry key for the Nectere chunk generator settings.
     */
    val NECTERE_CHUNK_GENERATOR_SETTINGS_KEY =
        RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, HotMConstants.identifier("nectere"))

    /**
     * ChunkGeneratorType preset for the Nectere dimension.
     */
    val NECTERE_CHUNK_GENERATOR_SETTINGS_BUILTIN = Registry.register(
        BuiltinRegistries.CHUNK_GENERATOR_SETTINGS,
        NECTERE_CHUNK_GENERATOR_SETTINGS_KEY.value,
        DimensionAdditions.createChunkGeneratorSettings(
            StructuresConfig(false),
            GenerationShapeConfig(
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
            false
        )
    )

    /**
     * Biome source preset for the Nectere dimension.
     */
    val NECTERE_BIOME_SOURCE_PRESET =
        MultiNoiseBiomeSource.Preset(HotMConstants.identifier("nectere")) { preset, registry, seed ->
            DimensionAdditions.createMultiNoiseBiomeSource(
                seed,
                HotMBiomes.biomeNoise().entries.stream().map<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> { entry ->
                    Pair.of(entry.value, Supplier { registry.getOrThrow(entry.key) })
                }.collect(ImmutableList.toImmutableList()),
                Optional.of(Pair.of(registry, preset))
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
            HotMConstants.identifier("nectere"),
            NectereChunkGenerator.CODEC
        )

        DimensionAdditions.addDimension(
            NECTERE_OPTIONS_KEY,
            NECTERE_TYPE_KEY,
            NECTERE_TYPE
        ) { biomes, generatorSettings, seed -> createNectereGenerator(biomes, generatorSettings, seed) }

        DimensionAdditions.registerDefaultPlacer(
            NECTERE_KEY
        ) { oldEntity, destination -> getGenericTeleportTarget(oldEntity, destination) }
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
     * Performs a teleportation between the Overworld and the Nectere.
     *
     * @return whether the portal that attempted to teleport the entity is in a valid location.
     */
    fun attemptNectereTeleportation(entity: Entity, world: World, portalPos: BlockPos): Boolean {
        return (world as? ServerWorld)?.let { serverWorld ->
            if ((entity as EntityAccessor).netherPortalCooldown > 0) {
                entity.netherPortalCooldown = entity.defaultNetherPortalCooldown

                true
            } else {
                val server = serverWorld.server

                if (world.registryKey == NECTERE_KEY) {
                    val newWorld = getCorrespondingNonNectereWorld(serverWorld, portalPos)
                    val newPoses = getCorrespondingNonNectereCoords(serverWorld, portalPos).collect(Collectors.toList())

                    attemptTeleport(newWorld, newPoses, entity)
                } else {
                    val nectereWorld = getNectereWorld(server)
                    val newPoses =
                        getCorrespondingNectereCoords(serverWorld, portalPos, nectereWorld).collect(Collectors.toList())

                    attemptTeleport(nectereWorld, newPoses, entity)
                }
            }
        } ?: true
    }

    /**
     * Attempts to perform a teleportation of the target entity to the destination world with a list of destination
     * positions.
     */
    private fun attemptTeleport(
        destWorld: ServerWorld?,
        destPoses: List<BlockPos>,
        entity: Entity
    ): Boolean {
        return if (destWorld != null && destPoses.isNotEmpty()) {
            pregenPortals(destWorld, destPoses)

            var destPos = findNecterePortal(destWorld, destPoses)

            if (destPos == null && HotMConfig.CONFIG.generateMissingPortals) {
                destPos = createNecterePortal(destWorld, destPoses)
            }

            val finalPos = destPos

            if (finalPos != null) {
                val res = DimensionAdditions.teleport(
                    entity,
                    destWorld
                ) { oldEntity, _ ->
                    getTeleportTarget(
                        oldEntity,
                        finalPos
                    )
                }

                if (res != null) {
                    (res as EntityAccessor).netherPortalCooldown = res.defaultNetherPortalCooldown

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * Makes sure all NecterePortalSpawnerBlockEntities in the destination chunks have generated their receiving portals.
     */
    fun pregenPortals(world: ServerWorld, newPoses: List<BlockPos>) {
        if (world.registryKey != NECTERE_KEY) {
            // look for and run nectere portal spawner block entities
            val checked = mutableSetOf<ChunkPos>()

            for (pos in newPoses) {
                val chunkPos = ChunkPos(pos)
                if (!checked.contains(chunkPos)) {
                    checked.add(chunkPos)

                    val spawnerPos = BlockPos(chunkPos.startX, 0, chunkPos.startZ)

                    (world.getBlockEntity(spawnerPos) as? NecterePortalSpawnerBlockEntity)?.spawn()
                }
            }
        }
    }

    /**
     * Attempt to find a Nectere portal among a list of destination positions.
     */
    fun findNecterePortal(world: WorldView, newPoses: List<BlockPos>): BlockPos? {
        for (offset in 0 until 256) {
            for (init in newPoses) {
                val up = init.up(offset)
                val down = init.down(offset)

                if (!World.isHeightInvalid(up) && world.getBlockState(up).block == HotMBlocks.NECTERE_PORTAL) {
                    return findPortalBase(world, up)
                }

                if (!World.isHeightInvalid(down) && world.getBlockState(down).block == HotMBlocks.NECTERE_PORTAL) {
                    return findPortalBase(world, down)
                }
            }
        }

        return null
    }

    /**
     * When given a portal block, finds the base portal block.
     */
    private fun findPortalBase(world: WorldView, portalPos: BlockPos): BlockPos {
        val mut = portalPos.mutableCopy()
        while (world.getBlockState(mut.down()).block == HotMBlocks.NECTERE_PORTAL) {
            mut.move(Direction.DOWN)
        }
        return mut.toImmutable()
    }

    /**
     * Creates a Nectere portal at an optimal position among a list of destination positions and returns the location of
     * that portal.
     */
    private fun createNecterePortal(world: WorldAccess, newPoses: List<BlockPos>): BlockPos {
        val rand = Random()
        val portalXZ = newPoses[rand.nextInt(newPoses.size)]
        val portalPos = BlockPos(
            portalXZ.x,
            NecterePortalGen.getPortalStructureY(
                world,
                portalXZ.x,
                portalXZ.z,
                rand
            ) + NecterePortalGen.PORTAL_OFFSET_Y,
            portalXZ.z
        )
        val structurePos = NecterePortalGen.unPortalPos(portalPos)

        NecterePortalGen.generate(world, structurePos)

        return portalPos
    }

    /**
     * Finds the teleportation destination block in the Nectere dimension.
     */
    private fun getTeleportTarget(
        oldEntity: Entity,
        destinationPos: BlockPos
    ): TeleportTarget {
        return TeleportTarget(
            Vec3d.of(destinationPos).add(0.5, 0.0, 0.5),
            Vec3d.ZERO,
            oldEntity.yaw,
            oldEntity.pitch
        )
    }

    /**
     * Finds the teleportation destination block when the portal location is not known.
     */
    private fun getGenericTeleportTarget(
        oldEntity: Entity,
        destination: ServerWorld
    ): TeleportTarget {
        // load chunk so heightmap loading works properly
        destination.getChunk(oldEntity.blockPos)

        val position = destination.getTopPosition(Heightmap.Type.WORLD_SURFACE, oldEntity.blockPos)
        return TeleportTarget(Vec3d.of(position).add(0.5, 0.5, 0.5), Vec3d.ZERO, oldEntity.yaw, oldEntity.pitch)
    }

    /**
     * Gets the Nectere dimension from the server, forcibly adding it if it does not exist already.
     */
    fun getNectereWorld(server: MinecraftServer): ServerWorld {
        return server.getWorld(NECTERE_KEY) ?: throw IllegalStateException("Nectere dimension was never added!")
    }

    /**
     * Loads the current biomes from the registry and makes sure that all NectereBiomes are accounted for. This also
     * makes sure new biomes that get added to the registry later and are NectereBiomes are also accounted for.
     */
    fun findBiomes() {
        for (biomeData in HotMBiomes.biomeData().values) {
            if (biomeData.isPortalable) {
                NECTERE_PORTAL_BIOMES.put(biomeData.targetWorld, biomeData)
            }
        }

        // TODO NectereBiomeData mod-compat/API
    }

    /**
     * Gets all Nectere-side block locations that could connect to the current non-Nectere-side block location.
     */
    fun getCorrespondingNectereCoords(
        currentWorld: ServerWorld,
        currentPos: BlockPos,
        nectereWorld: ServerWorld
    ): Stream<BlockPos> {
        return NECTERE_PORTAL_BIOMES.get(currentWorld.registryKey).stream().flatMap { nectereBiome ->
            if (nectereBiome.isPortalable) {
                if (nectereBiome.coordinateMultiplier < 1) {
                    // if the overworld block corresponds to multiple nectere blocks, then just give all of them and
                    // check they're in the right biome
                    IntStream.range(
                        floor(currentPos.x.toDouble() / nectereBiome.coordinateMultiplier),
                        floor((currentPos.x + 1).toDouble() / nectereBiome.coordinateMultiplier)
                    ).mapToObj { it }.flatMap { x ->
                        IntStream.range(
                            floor(currentPos.z.toDouble() / nectereBiome.coordinateMultiplier),
                            floor((currentPos.z + 1).toDouble() / nectereBiome.coordinateMultiplier)
                        ).mapToObj { z -> BlockPos(x, currentPos.y, z) }
                    }.filter { nectereBiome.biome == nectereWorld.method_31081(it).orElse(null) }
                } else {
                    val newPos = BlockPos(
                        currentPos.x.toDouble() / nectereBiome.coordinateMultiplier,
                        currentPos.y.toDouble(),
                        currentPos.z.toDouble() / nectereBiome.coordinateMultiplier
                    )

                    // make sure the chunk is loaded
                    nectereWorld.getBlockState(newPos)
                    if (nectereBiome.biome == nectereWorld.method_31081(newPos).orElse(null)) {
                        Stream.of(newPos)
                    } else {
                        Stream.empty()
                    }
                }
            } else {
                Stream.empty()
            }
        }
    }

    /**
     * Gets the non-Nectere-coordinates of all Nectere portals within this chunk.
     */
    fun getNonNecterePortalCoords(
        registryManager: DynamicRegistryManager,
        currentWorldKey: RegistryKey<World>,
        currentPos: ChunkPos,
        heightFn: (Int, Int) -> Int,
        nectereWorld: ServerWorld
    ): Stream<BlockPos> {
        if (currentWorldKey == NECTERE_KEY) {
            throw IllegalArgumentException("Cannot get non-Nectere portal gen coords in a Nectere world")
        }

        return NECTERE_PORTAL_BIOMES.get(currentWorldKey).stream().flatMap { nectereBiome ->
            if (nectereBiome.isPortalable) {
                if (nectereBiome.coordinateMultiplier < 1.0) {
                    IntStream.range(
                        floor(currentPos.x.toDouble() / nectereBiome.coordinateMultiplier),
                        floor((currentPos.x + 1).toDouble() / nectereBiome.coordinateMultiplier)
                    ).mapToObj { it }.flatMap { x ->
                        IntStream.range(
                            floor(currentPos.z.toDouble() / nectereBiome.coordinateMultiplier),
                            floor((currentPos.z + 1).toDouble() / nectereBiome.coordinateMultiplier)
                        ).mapToObj { z -> ChunkPos(x, z) }
                    }.flatMap { necterePos ->
                        getNonNecterePortalCoordsForBiome(
                            registryManager,
                            currentPos,
                            heightFn,
                            nectereWorld,
                            nectereBiome,
                            necterePos
                        )
                    }
                } else {
                    val necterePos = ChunkPos(
                        floor(currentPos.x.toDouble() / nectereBiome.coordinateMultiplier),
                        floor(currentPos.z.toDouble() / nectereBiome.coordinateMultiplier)
                    )

                    getNonNecterePortalCoordsForBiome(
                        registryManager,
                        currentPos,
                        heightFn,
                        nectereWorld,
                        nectereBiome,
                        necterePos
                    )
                }
            } else {
                Stream.empty()
            }
        }
    }

    /**
     * Gets the non-Nectere-side location of all Nectere portals for the given Nectere-side chunk and Nectere-side biome.
     */
    private fun getNonNecterePortalCoordsForBiome(
        registryManager: DynamicRegistryManager,
        currentPos: ChunkPos,
        heightFn: (Int, Int) -> Int,
        nectereWorld: ServerWorld,
        nectereBiomeData: NectereBiomeData,
        necterePos: ChunkPos
    ): Stream<BlockPos> {
        val chunkRandom = ChunkRandom()
        val structureConfig =
            nectereWorld.chunkManager.chunkGenerator.structuresConfig.getForType(HotMStructureFeatures.NECTERE_PORTAL)
        val portalChunk = HotMStructureFeatures.NECTERE_PORTAL.getStartChunk(
            structureConfig,
            nectereWorld.seed,
            chunkRandom,
            necterePos.x,
            necterePos.z
        )

        val chunkGenerator = nectereWorld.chunkManager.chunkGenerator
        val biomeSource = chunkGenerator.biomeSource
        val biomeRegistry = registryManager[Registry.BIOME_KEY]

        @Suppress("cast_never_succeeds")
        if ((HotMStructureFeatures.NECTERE_PORTAL as StructureFeatureAccessor).callShouldStartAt(
                chunkGenerator,
                biomeSource,
                nectereWorld.seed,
                chunkRandom,
                necterePos.x,
                necterePos.z,
                biomeRegistry[nectereBiomeData.biome],
                necterePos,
                FeatureConfig.DEFAULT
            )
        ) {
            val necterePortalPos =
                BlockPos(NecterePortalGen.getPortalX(portalChunk.x), 64, NecterePortalGen.getPortalZ(portalChunk.z))

            val biome = biomeSource.getBiomeForNoiseGen(
                necterePortalPos.x shr 2,
                necterePortalPos.y shr 2,
                necterePortalPos.z shr 2
            )

            val biomeId = biomeRegistry.getKey(biome).orElse(null)

            if (nectereBiomeData.biome == biomeId) {
                val resX = floor(necterePortalPos.x.toDouble() * nectereBiomeData.coordinateMultiplier)
                val resZ = floor(necterePortalPos.z.toDouble() * nectereBiomeData.coordinateMultiplier)

                return if (resX shr 4 == currentPos.x && resZ shr 4 == currentPos.z) {
                    val resPos = NecterePortalGen.unPortalPos(
                        BlockPos(
                            resX,
                            heightFn(resX, resZ) + NecterePortalGen.PORTAL_OFFSET_Y,
                            resZ
                        )
                    )

                    Stream.of(resPos)
                } else {
                    Stream.empty()
                }
            } else {
                return Stream.empty()
            }
        } else {
            return Stream.empty()
        }
    }

    /**
     * Gets the non-Nectere-side block location that connects to the current Nectere-side block location.
     */
    fun getCorrespondingNonNectereCoords(nectereWorld: WorldAccess, necterePos: BlockPos): Stream<BlockPos> {
        val biomeKey = nectereWorld.method_31081(necterePos).orElse(null)

        return if (biomeKey != null && HotMBiomes.biomeData()
                .containsKey(biomeKey) && (HotMBiomes.biomeData()[biomeKey]
                ?: error("Null biome data")).isPortalable
        ) {
            val biomeData = HotMBiomes.biomeData()[biomeKey] ?: error("Null biome data")
            if (biomeData.coordinateMultiplier > 1) {
                IntStream.range(
                    floor(necterePos.x.toDouble() * biomeData.coordinateMultiplier),
                    floor((necterePos.x + 1).toDouble() * biomeData.coordinateMultiplier)
                ).mapToObj { it }.flatMap { x ->
                    IntStream.range(
                        floor(necterePos.z.toDouble() * biomeData.coordinateMultiplier),
                        floor((necterePos.z + 1).toDouble() * biomeData.coordinateMultiplier)
                    ).mapToObj { z -> BlockPos(x, necterePos.y, z) }
                }
            } else {
                Stream.of(
                    BlockPos(
                        necterePos.x.toDouble() * biomeData.coordinateMultiplier,
                        necterePos.y.toDouble(),
                        necterePos.z.toDouble() * biomeData.coordinateMultiplier
                    )
                )
            }
        } else {
            Stream.empty()
        }
    }

    /**
     * Gets the non-Nectere-side coordinate of a *generated* Nectere portal.
     */
    fun getBaseCorrespondingNonNectereCoords(nectereWorld: WorldAccess, necterePos: BlockPos): BlockPos? {
        val biomeKey = nectereWorld.method_31081(necterePos).orElse(null)

        return if (biomeKey != null && HotMBiomes.biomeData()
                .containsKey(biomeKey) && (HotMBiomes.biomeData()[biomeKey]
                ?: error("Null biome data")).isPortalable
        ) {
            val biomeData = HotMBiomes.biomeData()[biomeKey] ?: error("Null biome data")
            BlockPos(
                necterePos.x.toDouble() * biomeData.coordinateMultiplier,
                necterePos.y.toDouble(),
                necterePos.z.toDouble() * biomeData.coordinateMultiplier
            )
        } else {
            null
        }
    }

    /**
     * Gets the non-Nectere-side world that connects to the current Nectere-side block location.
     */
    fun getCorrespondingNonNectereWorld(nectereWorld: ServerWorld, necterePos: BlockPos): ServerWorld? {
        val server = nectereWorld.server
        val biomeKey = nectereWorld.method_31081(necterePos).orElse(null)

        return if (biomeKey != null && HotMBiomes.biomeData()
                .containsKey(biomeKey) && (HotMBiomes.biomeData()[biomeKey]
                ?: error("Null biome data")).isPortalable
        ) {
            val biomeData = HotMBiomes.biomeData()[biomeKey] ?: error("Null biome data")
            val world = server.getWorld(biomeData.targetWorld)
            if (world == null) {
                HotMLog.log.warn("Attempted to get non-existent world for Nectere biome with world key: ${biomeData.targetWorld}")
            }
            world
        } else {
            null
        }
    }

    /**
     * Locates a Nectere portal in a non-Nectere dimension.
     */
    fun locateNonNectereSidePortal(
        currentWorld: ServerWorld,
        currentPos: BlockPos,
        radius: Int,
        skipExistingChunks: Boolean
    ): BlockPos? {
        val nectereWorld = getNectereWorld(currentWorld.server)

        return NECTERE_PORTAL_BIOMES.get(currentWorld.registryKey).stream().flatMap { nectereBiome ->
            if (nectereBiome.isPortalable) {
                val necterePos = BlockPos(
                    currentPos.x.toDouble() / nectereBiome.coordinateMultiplier,
                    currentPos.y.toDouble(),
                    currentPos.z.toDouble() / nectereBiome.coordinateMultiplier
                )

                val foundPos = HotMStructureFeatures.NECTERE_PORTAL.locateNonNectereSidePortal(
                    nectereWorld,
                    nectereWorld.structureAccessor,
                    necterePos,
                    radius,
                    skipExistingChunks,
                    nectereWorld.seed,
                    nectereWorld.chunkManager.chunkGenerator.structuresConfig.getForType(HotMStructureFeatures.NECTERE_PORTAL)
                        ?: error("Null Nectere Portal structure config"),
                    nectereBiome.biome,
                    currentWorld
                )

                if (foundPos != null) {
                    Stream.of(foundPos)
                } else {
                    Stream.empty()
                }
            } else {
                Stream.empty()
            }
        }.min(Comparator.comparing<BlockPos, Double> { portalPos -> portalPos.getSquaredDistance(currentPos) })
            .orElse(null)
    }

    /**
     * Retro-generates the "nearest" Nectere portal.
     */
    fun retrogenNonNectereSidePortal(
        currentWorld: ServerWorld,
        currentPos: BlockPos,
        radius: Int
    ): RetrogenPortalResult {
        return locateNonNectereSidePortal(currentWorld, currentPos, radius, false)?.let { structurePos ->
            val portalPos = NecterePortalGen.portalPos(structurePos)
            val foundPos = findNecterePortal(currentWorld, listOf(portalPos))
            if (foundPos == null) {
                val genPos = createNecterePortal(currentWorld, listOf(portalPos))

                RetrogenPortalResult.Generated(NecterePortalGen.unPortalPos(genPos))
            } else {
                RetrogenPortalResult.Found(NecterePortalGen.unPortalPos(foundPos))
            }
        } ?: RetrogenPortalResult.Failure
    }

    /**
     * The possible results of portal retro-generation.
     */
    sealed class RetrogenPortalResult {
        object Failure : RetrogenPortalResult()
        data class Found(val blockPos: BlockPos) : RetrogenPortalResult()
        data class Generated(val blockPos: BlockPos) : RetrogenPortalResult()
    }
}
