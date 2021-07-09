package com.github.hotm.world.gen.chunk

import com.github.hotm.HotMConfig
import com.github.hotm.mixinapi.ChunkGeneratorSettingsAccess.*
import com.github.hotm.mixinapi.StructureWeightSamplerAccess
import com.github.hotm.world.HotMDimensions
import com.google.common.collect.Sets
import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Util
import net.minecraft.util.collection.Pool
import net.minecraft.util.dynamic.RegistryLookupCodec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.noise.*
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.ChunkRegion
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.SpawnHelper
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.SpawnSettings
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkSection
import net.minecraft.world.chunk.ProtoChunk
import net.minecraft.world.gen.*
import net.minecraft.world.gen.chunk.*
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.DoubleFunction
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

class NectereChunkGenerator private constructor(
    biomeSource: BiomeSource, biomeSource2: BiomeSource, private val genSeed: Long,
    private val settings: () -> ChunkGeneratorSettings, private val biomeRegistry: Registry<Biome>
) : ChunkGenerator(biomeSource, biomeSource2, settings().structuresConfig, genSeed) {

    companion object {
        val CODEC: Codec<NectereChunkGenerator> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<NectereChunkGenerator> ->
                instance
                    .group(
                        BiomeSource.CODEC.fieldOf("biome_source").forGetter(NectereChunkGenerator::biomeSource),
                        Codec.LONG.fieldOf("seed").stable().forGetter(NectereChunkGenerator::genSeed),
                        ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")
                            .forGetter { Supplier { it.settings() } },
                        RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(NectereChunkGenerator::biomeRegistry)
                    )
                    .apply(
                        instance,
                        instance.stable(Function4 { biomeSource, seed, genSettings, registry ->
                            NectereChunkGenerator(
                                if (HotMConfig.CONFIG.forceNectereBiomeSource) {
                                    HotMDimensions.NECTERE_BIOME_SOURCE_PRESET.getBiomeSource(registry, seed)
                                } else {
                                    biomeSource
                                },
                                seed,
                                { genSettings.get() },
                                registry
                            )
                        })
                    )
            }

        private val AIR: BlockState = Blocks.AIR.defaultState
        private val EMPTY: Array<BlockState> = Array(0) { AIR }
    }

    private val verticalNoiseResolution: Int
    private val horizontalNoiseResolution: Int
    private val noiseSizeX: Int
    private val noiseSizeY: Int
    private val noiseSizeZ: Int
    private val random: ChunkRandom
    private val lowerInterpolatedNoise: OctavePerlinNoiseSampler
    private val upperInterpolatedNoise: OctavePerlinNoiseSampler
    private val interpolationNoise: OctavePerlinNoiseSampler
    private val surfaceDepthNoise: NoiseSampler
    private val randomDensityOffset: OctavePerlinNoiseSampler
    private var islandNoiseOverride: SimplexNoiseSampler? = null
    private val defaultBlock: BlockState
    private val defaultFluid: BlockState
    private val terrainHeight: Int
    private val noiseColumnSampler: NectereColumnSampler
    private val deepslateSource: DeepslateBlockSource
    private val edgeDensityNoise: DoublePerlinNoiseSampler
    private val fluidLevelNoise: DoublePerlinNoiseSampler
    private val fluidTypeNoise: DoublePerlinNoiseSampler
    private val noodleCavesGenerator: NoodleCavesGenerator
    private val oreVeinGenerator: OreVeinGenerator

    init {
        val genSettings = settings()
        val shapeConfig = genSettings.generationShapeConfig
        terrainHeight = shapeConfig.height
        verticalNoiseResolution = shapeConfig.sizeVertical * 4
        horizontalNoiseResolution = shapeConfig.sizeHorizontal * 4
        defaultBlock = genSettings.defaultBlock
        defaultFluid = genSettings.defaultFluid
        noiseSizeX = 16 / horizontalNoiseResolution
        noiseSizeY = shapeConfig.height / verticalNoiseResolution
        noiseSizeZ = 16 / horizontalNoiseResolution
        random = ChunkRandom(genSeed)
        val interpolatedNoiseSampler = InterpolatedNoiseSampler(random)

        lowerInterpolatedNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        upperInterpolatedNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        interpolationNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-7, 0))

        surfaceDepthNoise = if (shapeConfig.hasSimplexSurfaceNoise()) OctaveSimplexNoiseSampler(
            random,
            IntStream.rangeClosed(-3, 0)
        ) else OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-3, 0))
        random.skip(2620)

        randomDensityOffset = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        islandNoiseOverride = if (shapeConfig.hasIslandNoiseOverride()) {
            val chunkRandom = ChunkRandom(genSeed)
            chunkRandom.skip(17292)
            SimplexNoiseSampler(chunkRandom)
        } else {
            null
        }


        edgeDensityNoise = DoublePerlinNoiseSampler.create(SimpleRandom(random.nextLong()), -3, 1.0)
        fluidLevelNoise = DoublePerlinNoiseSampler.create(SimpleRandom(random.nextLong()), -3, 1.0, 0.0, 2.0)
        fluidTypeNoise = DoublePerlinNoiseSampler.create(SimpleRandom(random.nextLong()), -1, 1.0, 0.0)

        val weightSampler = if (hasNoiseCaves(genSettings)) {
            NoiseCaveSampler(random, shapeConfig.minimumY / this.verticalNoiseResolution)
        } else {
            WeightSampler.DEFAULT
        }

        noiseColumnSampler = NectereColumnSampler(
            populationSource,
            horizontalNoiseResolution,
            verticalNoiseResolution,
            noiseSizeY,
            shapeConfig,
            interpolatedNoiseSampler,
            islandNoiseOverride,
            randomDensityOffset,
            weightSampler
        )
        // FIXME: DeepSlate should not be generating in the Nectere dimension. THIS IS A PLACEHOLDER.
        deepslateSource = DeepslateBlockSource(genSeed, defaultBlock, Blocks.DEEPSLATE.defaultState, genSettings)
        // FIXME: Copper and iron ore should not be generating in the Nectere dimension. THIS IS A PLACEHOLDER.
        oreVeinGenerator = OreVeinGenerator(
            genSeed,
            this.defaultBlock,
            this.horizontalNoiseResolution,
            this.verticalNoiseResolution,
            genSettings.generationShapeConfig.minimumY
        )
        noodleCavesGenerator = NoodleCavesGenerator(genSeed)
    }

    constructor(
        biomeSource: BiomeSource,
        l: Long,
        settings: () -> ChunkGeneratorSettings,
        registry: Registry<Biome>
    ) : this(
        biomeSource,
        biomeSource,
        l,
        settings,
        registry
    )

    private fun hasAquifers(): Boolean {
        return hasAquifers(settings())
    }

    override fun getCodec(): Codec<out ChunkGenerator> {
        return CODEC
    }

    @Environment(EnvType.CLIENT)
    override fun withSeed(seed: Long): ChunkGenerator {
        return NectereChunkGenerator(biomeSource.withSeed(seed), seed, settings, biomeRegistry)
    }

    fun matchesSettings(seed: Long, settingsKey: RegistryKey<ChunkGeneratorSettings?>?): Boolean {
        return seed == seed && settings().equals(settingsKey)
    }

    private fun sampleNoiseColumn(x: Int, z: Int, minY: Int, noiseSizeY: Int): DoubleArray {
        val ds = DoubleArray(noiseSizeY + 1)
        this.sampleNoiseColumn(ds, x, z, minY, noiseSizeY)
        return ds
    }

    private fun sampleNoiseColumn(buffer: DoubleArray, x: Int, z: Int, minY: Int, noiseSizeY: Int) {
        val generationShapeConfig = settings().generationShapeConfig
        this.noiseColumnSampler.sampleNoiseColumn(buffer, x, z, generationShapeConfig, this.seaLevel, minY, noiseSizeY)
    }

    override fun getHeight(x: Int, z: Int, heightmap: Heightmap.Type, world: HeightLimitView): Int {
        val i = max(settings().generationShapeConfig.minimumY, world.bottomY)
        val j = min(settings().generationShapeConfig.minimumY + settings().generationShapeConfig.height, world.topY)
        val k = MathHelper.floorDiv(i, verticalNoiseResolution)
        val l = MathHelper.floorDiv(j - i, verticalNoiseResolution)
        return if (l <= 0) world.bottomY else sampleHeightmap(
            x,
            z,
            null,
            heightmap.blockPredicate,
            k,
            l
        ).orElse(world.bottomY)
    }

    override fun getColumnSample(x: Int, z: Int, world: HeightLimitView): VerticalBlockSample {
        val i = max(settings().generationShapeConfig.minimumY, world.bottomY)
        val j = min(settings().generationShapeConfig.minimumY + settings().generationShapeConfig.height, world.topY)
        val k = MathHelper.floorDiv(i, verticalNoiseResolution)
        val l = MathHelper.floorDiv(j - i, verticalNoiseResolution)
        return if (l <= 0) {
            VerticalBlockSample(i, EMPTY)
        } else {
            val blockStates = arrayOfNulls<BlockState>(l * verticalNoiseResolution)
            sampleHeightmap(x, z, blockStates, null, k, l)
            VerticalBlockSample(i, blockStates)
        }
    }

    override fun getBlockSource(): BlockSource {
        return this.deepslateSource
    }

    private fun sampleHeightmap(
        x: Int,
        z: Int,
        states: Array<BlockState?>?,
        predicate: Predicate<BlockState>?,
        minY: Int,
        noiseSizeY: Int
    ): OptionalInt {
        val i = ChunkSectionPos.getSectionCoord(x)
        val j = ChunkSectionPos.getSectionCoord(z)
        val k = Math.floorDiv(x, horizontalNoiseResolution)
        val l = Math.floorDiv(z, horizontalNoiseResolution)
        val m = Math.floorMod(x, horizontalNoiseResolution)
        val n = Math.floorMod(z, horizontalNoiseResolution)
        val d = m.toDouble() / horizontalNoiseResolution.toDouble()
        val e = n.toDouble() / horizontalNoiseResolution.toDouble()
        val ds = arrayOf(
            this.sampleNoiseColumn(k, l, minY, noiseSizeY),
            this.sampleNoiseColumn(k, l + 1, minY, noiseSizeY),
            this.sampleNoiseColumn(k + 1, l, minY, noiseSizeY),
            this.sampleNoiseColumn(k + 1, l + 1, minY, noiseSizeY)
        )
        val aquiferSampler: AquiferSampler = this.createBlockSampler(minY, noiseSizeY, ChunkPos(i, j))
        for (o in noiseSizeY - 1 downTo 0) {
            val f = ds[0][o]
            val g = ds[1][o]
            val h = ds[2][o]
            val p = ds[3][o]
            val q = ds[0][o + 1]
            val r = ds[1][o + 1]
            val s = ds[2][o + 1]
            val t = ds[3][o + 1]
            for (u in verticalNoiseResolution - 1 downTo 0) {
                val v = u.toDouble() / verticalNoiseResolution.toDouble()
                val w = MathHelper.lerp3(v, d, e, f, q, h, s, g, r, p, t)
                val y = o * verticalNoiseResolution + u
                val aa = y + minY * verticalNoiseResolution
                val blockState = getBlockState(
                    StructureWeightSampler.INSTANCE, aquiferSampler,
                    deepslateSource, WeightSampler.DEFAULT, x, aa, z, w
                )
                if (states != null) {
                    states[y] = blockState
                }
                if (predicate != null && predicate.test(blockState)) {
                    return OptionalInt.of(aa + 1)
                }
            }
        }
        return OptionalInt.empty()
    }

    private fun createBlockSampler(startY: Int, deltaY: Int, pos: ChunkPos): AquiferSampler {
        return if (!this.hasAquifers()) AquiferSampler.seaLevel(
            this.seaLevel,
            defaultFluid
        ) else AquiferSampler.aquifer(
            pos,
            this.edgeDensityNoise,
            this.fluidLevelNoise,
            this.fluidTypeNoise,
            settings(),
            noiseColumnSampler,
            startY * verticalNoiseResolution,
            deltaY * verticalNoiseResolution
        )
    }

    private fun getBlockState(
        structures: StructureWeightSampler,
        aquiferSampler: AquiferSampler,
        blockInterpolator: BlockSource?,
        weightSampler: WeightSampler,
        i: Int,
        j: Int,
        k: Int,
        d: Double
    ): BlockState {
        var e = MathHelper.clamp(d / 200.0, -1.0, 1.0)
        e = e / 2.0 - e * e * e / 24.0
        e = weightSampler.sample(e, i, j, k)
        e += StructureWeightSamplerAccess.getWeight(structures, i, j, k)
        return aquiferSampler.apply(blockInterpolator, i, j, k, e)
    }

    override fun buildSurface(region: ChunkRegion, chunk: Chunk) {
        val chunkPos = chunk.pos
        val i = chunkPos.x
        val j = chunkPos.z
        val chunkRandom = ChunkRandom()
        chunkRandom.setTerrainSeed(i, j)
        val chunkPos2 = chunk.pos
        val k = chunkPos2.startX
        val l = chunkPos2.startZ
        val mutable = BlockPos.Mutable()

        for (m in 0..15) {
            for (n in 0..15) {
                val o = k + m
                val p = l + n
                val q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1
                val e = surfaceDepthNoise.sample(
                    o.toDouble() * 0.0625, p.toDouble() * 0.0625, 0.0625, m.toDouble() * 0.0625
                ) * 15.0
                val r = settings().minSurfaceLevel
                region.getBiome(mutable.set(k + m, q, l + n)).buildSurface(
                    chunkRandom, chunk, o, p, q, e, defaultBlock,
                    defaultFluid, this.seaLevel, r, region.seed
                )
            }
        }

        buildBedrock(chunk, chunkRandom)
    }

    private fun buildBedrock(chunk: Chunk, random: Random) {
        val genSettings = settings()
        val mutable = BlockPos.Mutable()
        val i = chunk.pos.startX
        val j = chunk.pos.startZ
        val k = genSettings.bedrockFloorY
        val l = terrainHeight - 1 - genSettings.bedrockCeilingY
        val bl = l + 4 >= 0 && l < terrainHeight
        val bl2 = k + 4 >= 0 && k < terrainHeight

        if (bl || bl2) {
            for (blockPos in BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
                if (bl) {
                    var o = 0
                    while (o < 5) {
                        if (o <= random.nextInt(5)) {
                            chunk.setBlockState(
                                mutable.set(blockPos.x, l - o, blockPos.z),
                                Blocks.BEDROCK.defaultState, false
                            )
                        }
                        ++o
                    }
                }

                if (bl2) {
                    var o = 4
                    while (o >= 0) {
                        if (o <= random.nextInt(5)) {
                            chunk.setBlockState(
                                mutable.set(blockPos.x, k + o, blockPos.z),
                                Blocks.BEDROCK.defaultState, false
                            )
                        }
                        --o
                    }
                }
            }
        }
    }

    override fun populateNoise(
        executor: Executor,
        accessor: StructureAccessor,
        chunk: Chunk
    ): CompletableFuture<Chunk> {
        val generationShapeConfig = settings().generationShapeConfig
        val i = max(generationShapeConfig.minimumY, chunk.bottomY)
        val j = min(generationShapeConfig.minimumY + generationShapeConfig.height, chunk.topY)
        val k = MathHelper.floorDiv(i, verticalNoiseResolution)
        val l = MathHelper.floorDiv(j - i, verticalNoiseResolution)
        return if (l <= 0) {
            CompletableFuture.completedFuture(chunk)
        } else {
            val m = chunk.getSectionIndex(l * verticalNoiseResolution - 1 + i)
            val n = chunk.getSectionIndex(i)
            CompletableFuture.supplyAsync({
                val set = Sets.newHashSet<ChunkSection>()
                val var17: Chunk
                try {
                    var mx = m
                    while (true) {
                        if (mx < n) {
                            var17 = populateNoise(accessor, chunk, k, l)
                            break
                        }
                        val chunkSection = chunk.getSection(mx)
                        chunkSection.lock()
                        set.add(chunkSection)
                        --mx
                    }
                } finally {
                    for (chunkSection3 in set) {
                        chunkSection3.unlock()
                    }
                }
                var17
            }, Util.getMainWorkerExecutor())
        }
    }

    private fun populateNoise(accessor: StructureAccessor, chunk: Chunk, startY: Int, noiseSizeY: Int): Chunk {
        val heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG)
        val heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG)
        val chunkPos = chunk.pos
        val i = chunkPos.startX
        val j = chunkPos.startZ
        val structureWeightSampler = StructureWeightSamplerAccess.create(accessor, chunk)
        val aquiferSampler: AquiferSampler = this.createBlockSampler(startY, noiseSizeY, chunkPos)
        val noiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, chunkPos, startY
        ) { buffer: DoubleArray, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            this.sampleNoiseColumn(
                buffer, x, z, minY, noiseSizeY
            )
        }
        val list = mutableListOf(noiseInterpolator)
        Objects.requireNonNull(list)
        val consumer = Consumer { e: NoiseInterpolator -> list.add(e) }
        val doubleFunction: DoubleFunction<BlockSource> = this.createBlockSourceFactory(startY, chunkPos, consumer)
        val doubleFunction2: DoubleFunction<WeightSampler> = this.createWeightSamplerFactory(startY, chunkPos, consumer)
        list.forEach(Consumer { obj: NoiseInterpolator -> obj.sampleStartNoise() })
        val mutable = BlockPos.Mutable()
        for (k in 0 until noiseSizeX) {
            list.forEach(Consumer { noiseInterpolatorx: NoiseInterpolator ->
                noiseInterpolatorx.sampleEndNoise(
                    k
                )
            })
            for (m in 0 until noiseSizeZ) {
                var chunkSection = chunk.getSection(chunk.countVerticalSections() - 1)
                for (n in noiseSizeY - 1 downTo 0) {
                    list.forEach(
                        Consumer { noiseInterpolatorx: NoiseInterpolator ->
                            noiseInterpolatorx.sampleNoiseCorners(
                                n, m
                            )
                        })
                    for (q in verticalNoiseResolution - 1 downTo 0) {
                        val r = (startY + n) * verticalNoiseResolution + q
                        val s = r and 15
                        val t = chunk.getSectionIndex(r)
                        if (chunk.getSectionIndex(chunkSection.yOffset) != t) {
                            chunkSection = chunk.getSection(t)
                        }
                        val d = q.toDouble() / verticalNoiseResolution.toDouble()
                        list.forEach(Consumer { noiseInterpolatorx: NoiseInterpolator ->
                            noiseInterpolatorx.sampleNoiseY(
                                d
                            )
                        })
                        for (u in 0 until horizontalNoiseResolution) {
                            val v = i + k * horizontalNoiseResolution + u
                            val w = v and 15
                            val e = u.toDouble() / horizontalNoiseResolution.toDouble()
                            list.forEach(
                                Consumer { noiseInterpolatorx: NoiseInterpolator ->
                                    noiseInterpolatorx.sampleNoiseX(
                                        e
                                    )
                                })
                            for (x in 0 until horizontalNoiseResolution) {
                                val y = j + m * horizontalNoiseResolution + x
                                val z = y and 15
                                val f = x.toDouble() / horizontalNoiseResolution.toDouble()
                                val g = noiseInterpolator.sampleNoise(f)
                                val blockState = getBlockState(
                                    structureWeightSampler, aquiferSampler, doubleFunction.apply(f) as BlockSource,
                                    doubleFunction2.apply(f) as WeightSampler, v, r, y, g
                                )
                                if (blockState !== AIR) {
                                    if (blockState.luminance != 0 && chunk is ProtoChunk) {
                                        mutable[v, r] = y
                                        chunk.addLightSource(mutable)
                                    }
                                    chunkSection.setBlockState(w, s, z, blockState, false)
                                    heightmap.trackUpdate(w, r, z, blockState)
                                    heightmap2.trackUpdate(w, r, z, blockState)
                                    if (aquiferSampler.needsFluidTick() && !blockState.fluidState.isEmpty) {
                                        mutable[v, r] = y
                                        chunk.fluidTickScheduler.schedule(mutable, blockState.fluidState.fluid, 0)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            list.forEach(Consumer { obj: NoiseInterpolator -> obj.swapBuffers() })
        }
        return chunk
    }

    private fun createWeightSamplerFactory(
        minY: Int,
        pos: ChunkPos,
        consumer: Consumer<NoiseInterpolator>
    ): DoubleFunction<WeightSampler> {
        return if (!hasNoodleCaves(settings())) {
            DoubleFunction { WeightSampler.DEFAULT }
        } else {
            val noodleCavesSampler = NoodleCavesSampler(pos, minY)
            noodleCavesSampler.feed(consumer)
            Objects.requireNonNull(noodleCavesSampler)
            DoubleFunction { deltaZ: Double -> noodleCavesSampler.setDeltaZ(deltaZ) }
        }
    }

    private fun createBlockSourceFactory(
        minY: Int,
        pos: ChunkPos,
        consumer: Consumer<NoiseInterpolator>
    ): DoubleFunction<BlockSource> {
        return if (!hasOreVeins(settings())) {
            DoubleFunction { deepslateSource }
        } else {
            val oreVeinSource = OreVeinSource(pos, minY, genSeed + 1L)
            oreVeinSource.feed(consumer)
            val blockSource = BlockSource { i: Int, j: Int, k: Int ->
                val blockState: BlockState = oreVeinSource.sample(i, j, k)
                if (blockState !== defaultBlock) blockState else deepslateSource.sample(i, j, k)
            }
            DoubleFunction { deltaZ: Double ->
                oreVeinSource.setDeltaZ(deltaZ)
                blockSource
            }
        }
    }

    override fun createAquiferSampler(chunk: Chunk): AquiferSampler {
        val chunkPos = chunk.pos
        val i = max(settings().generationShapeConfig.minimumY, chunk.bottomY)
        val j = MathHelper.floorDiv(i, verticalNoiseResolution)
        return createBlockSampler(j, noiseSizeY, chunkPos)
    }

    override fun getWorldHeight(): Int {
        return terrainHeight
    }

    override fun getSeaLevel(): Int {
        return settings().seaLevel
    }

    override fun getMinimumY(): Int {
        return settings().generationShapeConfig.minimumY
    }

    override fun getEntitySpawnList(
        biome: Biome, accessor: StructureAccessor, group: SpawnGroup,
        pos: BlockPos
    ): Pool<SpawnSettings.SpawnEntry> {
        if (accessor.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
            if (group == SpawnGroup.MONSTER) {
                return StructureFeature.SWAMP_HUT.monsterSpawns
            }
            if (group == SpawnGroup.CREATURE) {
                return StructureFeature.SWAMP_HUT.creatureSpawns
            }
        }

        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                return StructureFeature.PILLAGER_OUTPOST.monsterSpawns
            }
            if (accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()) {
                return StructureFeature.MONUMENT.monsterSpawns
            }
            if (accessor.getStructureAt(pos, true, StructureFeature.FORTRESS).hasChildren()) {
                return StructureFeature.FORTRESS.monsterSpawns
            }
        }

        return if (group == SpawnGroup.UNDERGROUND_WATER_CREATURE && accessor.getStructureAt(
                pos,
                false,
                StructureFeature.MONUMENT
            ).hasChildren()
        ) {
            StructureFeature.MONUMENT.undergroundWaterCreatureSpawns
        } else {
            super.getEntitySpawnList(biome, accessor, group, pos)
        }
    }

    override fun populateEntities(region: ChunkRegion) {
        if (!isMobGenerationDisabled(settings())) {
            val centerChunk = region.centerPos
            val biome = region.getBiome(centerChunk.startPos)
            val chunkRandom = ChunkRandom()
            chunkRandom.setPopulationSeed(region.seed, centerChunk.startX, centerChunk.startZ)
            SpawnHelper.populateEntities(region, biome, centerChunk, chunkRandom)
        }
    }

    inner class NoodleCavesSampler(pos: ChunkPos, minY: Int) : WeightSampler {
        private val frequencyNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray?, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            noodleCavesGenerator.sampleFrequencyNoise(buffer, x, z, minY, noiseSizeY)
        }
        private val weightReducingNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray?, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            noodleCavesGenerator.sampleWeightReducingNoise(buffer, x, z, minY, noiseSizeY)
        }
        private val firstWeightNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray?, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            noodleCavesGenerator.sampleFirstWeightNoise(buffer, x, z, minY, noiseSizeY)
        }
        private val secondWeightNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray?, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            noodleCavesGenerator.sampleSecondWeightNoise(buffer, x, z, minY, noiseSizeY)
        }

        private var deltaZ = 0.0

        fun setDeltaZ(deltaZ: Double): WeightSampler {
            this.deltaZ = deltaZ
            return this
        }

        override fun sample(weight: Double, x: Int, y: Int, z: Int): Double {
            val d = frequencyNoiseInterpolator.sampleNoise(deltaZ)
            val e = weightReducingNoiseInterpolator.sampleNoise(deltaZ)
            val f = firstWeightNoiseInterpolator.sampleNoise(deltaZ)
            val g = secondWeightNoiseInterpolator.sampleNoise(deltaZ)
            return noodleCavesGenerator.sampleWeight(weight, x, y, z, d, e, f, g, minimumY)
        }

        fun feed(f: Consumer<NoiseInterpolator>) {
            f.accept(frequencyNoiseInterpolator)
            f.accept(weightReducingNoiseInterpolator)
            f.accept(firstWeightNoiseInterpolator)
            f.accept(secondWeightNoiseInterpolator)
        }
    }

    inner class OreVeinSource(pos: ChunkPos, minY: Int, private val seed: Long) : BlockSource {
        private val oreFrequencyNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            oreVeinGenerator.sampleOreFrequencyNoise(buffer, x, z, minY, noiseSizeY)
        }
        private val firstOrePlacementNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            oreVeinGenerator.sampleFirstOrePlacementNoise(buffer, x, z, minY, noiseSizeY)
        }
        private val secondOrePlacementNoiseInterpolator: NoiseInterpolator = NoiseInterpolator(
            noiseSizeX, noiseSizeY, noiseSizeZ, pos, minY
        ) { buffer: DoubleArray, x: Int, z: Int, minY: Int, noiseSizeY: Int ->
            oreVeinGenerator.sampleSecondOrePlacementNoise(buffer, x, z, minY, noiseSizeY)
        }

        private var deltaZ = 0.0
        private val random = ChunkRandom()

        fun feed(f: Consumer<NoiseInterpolator>) {
            f.accept(oreFrequencyNoiseInterpolator)
            f.accept(firstOrePlacementNoiseInterpolator)
            f.accept(secondOrePlacementNoiseInterpolator)
        }

        fun setDeltaZ(deltaZ: Double) {
            this.deltaZ = deltaZ
        }

        override fun sample(x: Int, y: Int, z: Int): BlockState {
            val d = oreFrequencyNoiseInterpolator.sampleNoise(deltaZ)
            val e = firstOrePlacementNoiseInterpolator.sampleNoise(deltaZ)
            val f = secondOrePlacementNoiseInterpolator.sampleNoise(deltaZ)
            random.setDeepslateSeed(seed, x, y, z)
            return oreVeinGenerator.sample(random, x, y, z, d, e, f)
        }
    }
}