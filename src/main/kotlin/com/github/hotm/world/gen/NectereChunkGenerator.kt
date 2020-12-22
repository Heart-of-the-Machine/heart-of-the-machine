package com.github.hotm.world.gen

import com.github.hotm.HotMConfig
import com.github.hotm.world.HotMDimensions
import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.SpawnGroup
import net.minecraft.structure.JigsawJunction
import net.minecraft.structure.PoolStructurePiece
import net.minecraft.structure.StructurePiece
import net.minecraft.structure.StructureStart
import net.minecraft.structure.pool.StructurePool
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.noise.NoiseSampler
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler
import net.minecraft.util.math.noise.SimplexNoiseSampler
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.RegistryLookupCodec
import net.minecraft.world.*
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.SpawnSettings
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ProtoChunk
import net.minecraft.world.gen.ChunkRandom
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.minecraft.world.gen.chunk.VerticalBlockSample
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.pow

class NectereChunkGenerator private constructor(
    biomeSource: BiomeSource, biomeSource2: BiomeSource, private val genSeed: Long,
    private val settings: () -> ChunkGeneratorSettings, private val biomeRegistry: Registry<Biome>
) : ChunkGenerator(biomeSource, biomeSource2, settings().structuresConfig, genSeed) {
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
        lowerInterpolatedNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        upperInterpolatedNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        interpolationNoise = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-7, 0))
        surfaceDepthNoise = if (shapeConfig.hasSimplexSurfaceNoise()) OctaveSimplexNoiseSampler(
            random,
            IntStream.rangeClosed(-3, 0)
        ) else OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-3, 0))
        random.consume(2620)
        randomDensityOffset = OctavePerlinNoiseSampler(random, IntStream.rangeClosed(-15, 0))
        islandNoiseOverride = if (shapeConfig.hasIslandNoiseOverride()) {
            val chunkRandom = ChunkRandom(genSeed)
            chunkRandom.consume(17292)
            SimplexNoiseSampler(chunkRandom)
        } else {
            null
        }
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

    override fun getCodec(): Codec<out ChunkGenerator> {
        return CODEC
    }

    @Environment(EnvType.CLIENT)
    override fun withSeed(seed: Long): ChunkGenerator {
        return NectereChunkGenerator(biomeSource.withSeed(seed), seed, settings, biomeRegistry)
    }

    fun method_28548(seed: Long, registryKey: RegistryKey<ChunkGeneratorSettings?>?): Boolean {
        return genSeed == seed && settings().equals(registryKey)
    }

    private fun sampleNoise(
        x: Int, y: Int, z: Int, horizontalScale: Double, verticalScale: Double,
        horizontalStretch: Double, verticalStretch: Double
    ): Double {
        var lower = 0.0
        var upper = 0.0
        var interpolation = 0.0
        var fineness = 1.0
        for (octave in 0..15) {
            val scaledX = OctavePerlinNoiseSampler.maintainPrecision(x.toDouble() * horizontalScale * fineness)
            val scaledY = OctavePerlinNoiseSampler.maintainPrecision(y.toDouble() * verticalScale * fineness)
            val scaledZ = OctavePerlinNoiseSampler.maintainPrecision(z.toDouble() * horizontalScale * fineness)
            val verticalFineness = verticalScale * fineness
            val lowerNoiseSampler = lowerInterpolatedNoise.getOctave(octave)
            if (lowerNoiseSampler != null) {
                lower += lowerNoiseSampler.sample(
                    scaledX,
                    scaledY,
                    scaledZ,
                    verticalFineness,
                    y.toDouble() * verticalFineness
                ) / fineness
            }
            val upperNoiseSampler = upperInterpolatedNoise.getOctave(octave)
            if (upperNoiseSampler != null) {
                upper += upperNoiseSampler.sample(
                    scaledX,
                    scaledY,
                    scaledZ,
                    verticalFineness,
                    y.toDouble() * verticalFineness
                ) / fineness
            }
            if (octave < 8) {
                val interpolationSampler = interpolationNoise.getOctave(octave)
                if (interpolationSampler != null) {
                    interpolation += interpolationSampler
                        .sample(
                            OctavePerlinNoiseSampler.maintainPrecision(x.toDouble() * horizontalStretch * fineness),
                            OctavePerlinNoiseSampler.maintainPrecision(y.toDouble() * verticalStretch * fineness),
                            OctavePerlinNoiseSampler.maintainPrecision(z.toDouble() * horizontalStretch * fineness),
                            verticalStretch * fineness, y.toDouble() * verticalStretch * fineness
                        ) / fineness
                }
            }
            fineness /= 2.0
        }
        return MathHelper.clampedLerp(lower / 512.0, upper / 512.0, (interpolation / 10.0 + 1.0) / 2.0)
    }

    private fun sampleNoiseColumn(x: Int, z: Int): DoubleArray {
        val ds = DoubleArray(noiseSizeY + 1)
        this.sampleNoiseColumn(ds, x, z)
        return ds
    }

    private fun sampleNoiseColumn(buffer: DoubleArray, x: Int, z: Int) {
        val shapeConfig = settings().generationShapeConfig

        val biomeDepth: Double
        val biomeScale: Double
        if (islandNoiseOverride != null) {
            biomeDepth = TheEndBiomeSource.getNoiseAt(islandNoiseOverride, x, z) - 8.0f.toDouble()
            biomeScale = if (biomeDepth > 0.0) {
                0.25
            } else {
                1.0
            }
        } else {
            var scaleSum = 0.0f
            var depthSum = 0.0f
            var weightSum = 0.0f
            val l = biomeSource.getBiomeForNoiseGen(x, seaLevel, z).depth
            for (m in -2..2) {
                for (n in -2..2) {
                    val biome = biomeSource.getBiomeForNoiseGen(x + m, seaLevel, z + n)
                    val blockDepth = biome.depth
                    val blockScale = biome.scale
                    var adjustedBlockDepth: Float
                    var asjustedBlockScale: Float
                    if (shapeConfig.isAmplified && blockDepth > 0.0f) {
                        adjustedBlockDepth = 1.0f + blockDepth * 2.0f
                        asjustedBlockScale = 1.0f + blockScale * 4.0f
                    } else {
                        adjustedBlockDepth = blockDepth
                        asjustedBlockScale = blockScale
                    }
                    val u = if (blockDepth > l) 0.5f else 1.0f
                    val weight =
                        u * field_24775[m + 2 + (n + 2) * 5] / (adjustedBlockDepth + 2.0f)
                    scaleSum += asjustedBlockScale * weight
                    depthSum += adjustedBlockDepth * weight
                    weightSum += weight
                }
            }
            val smoothedDepth = depthSum / weightSum
            val smoothedScale = scaleSum / weightSum
            val preBiomeDepth = smoothedDepth * 0.5f - 0.125f.toDouble()
            val preBiomeScale = smoothedScale * 0.9f + 0.1f.toDouble()
            biomeDepth = preBiomeDepth * 0.265625
            biomeScale = 96.0 / preBiomeScale
        }

        val xzScale = 684.412 * shapeConfig.sampling.xzScale
        val yScale = 684.412 * shapeConfig.sampling.yScale
        val xzFactor = xzScale / shapeConfig.sampling.xzFactor
        val yFactor = yScale / shapeConfig.sampling.yFactor
        val topTarget = shapeConfig.topSlide.target.toDouble()
        val topSize = shapeConfig.topSlide.size.toDouble()
        val topOffset = shapeConfig.topSlide.offset.toDouble()
        val bottomTarget = shapeConfig.bottomSlide.target.toDouble()
        val bottomSize = shapeConfig.bottomSlide.size.toDouble()
        val bottomOffset = shapeConfig.bottomSlide.offset.toDouble()
        val randomDensityOffset = if (shapeConfig.hasRandomDensityOffset()) getRandomDensityOffset(x, z) else 0.0
        val densityFactor = shapeConfig.densityFactor
        val densityOffset = shapeConfig.densityOffset

        for (sampleY in 0..noiseSizeY) {
            var density = sampleNoise(x, sampleY, z, xzScale, yScale, xzFactor, yFactor)
            val densityAdjustY = 1.0 - sampleY.toDouble() * 2.0 / noiseSizeY.toDouble() + randomDensityOffset

            // perform biome depth and scale adjustments before terrain depth and scale adjustments
            val preDensityAdjust = (densityAdjustY + biomeDepth) * biomeScale
            val densityAdjust = preDensityAdjust * densityFactor + densityOffset
//            val preDensityAdjust = densityAdjustY * densityFactor + densityOffset
//            val densityAdjust = (preDensityAdjust + biomeDepth) * biomeScale

            density += if (densityAdjust > 0.0) {
                densityAdjust * 4.0
            } else {
                densityAdjust
            }

            if (topSize > 0.0) {
                val ax = ((noiseSizeY - sampleY).toDouble() - topOffset) / topSize
                density = MathHelper.clampedLerp(topTarget, density, ax)
            }

            if (bottomSize > 0.0) {
                val ax = (sampleY.toDouble() - bottomOffset) / bottomSize
                density = MathHelper.clampedLerp(bottomTarget, density, ax)
            }

            buffer[sampleY] = density
        }
    }

    private fun getRandomDensityOffset(i: Int, j: Int): Double {
        val d = randomDensityOffset.sample(i * 200.toDouble(), 10.0, j * 200.toDouble(), 1.0, 0.0, true)
        val f: Double
        f = if (d < 0.0) {
            -d * 0.3
        } else {
            d
        }
        val g = f * 24.575625 - 2.0
        return if (g < 0.0) g * 0.009486607142857142 else g.coerceAtMost(1.0) * 0.006640625
    }

    override fun getHeight(x: Int, z: Int, heightmapType: Heightmap.Type): Int {
        return sampleHeightmap(x, z, null, heightmapType.blockPredicate)
    }

    override fun getColumnSample(x: Int, z: Int): BlockView {
        val blockStates =
            arrayOfNulls<BlockState>(noiseSizeY * verticalNoiseResolution)
        sampleHeightmap(x, z, blockStates, null)
        return VerticalBlockSample(blockStates)
    }

    private fun sampleHeightmap(
        x: Int, z: Int, states: Array<BlockState?>?,
        predicate: Predicate<BlockState?>?
    ): Int {
        val i = Math.floorDiv(x, horizontalNoiseResolution)
        val j = Math.floorDiv(z, horizontalNoiseResolution)
        val k = Math.floorMod(x, horizontalNoiseResolution)
        val l = Math.floorMod(z, horizontalNoiseResolution)
        val d = k.toDouble() / horizontalNoiseResolution.toDouble()
        val e = l.toDouble() / horizontalNoiseResolution.toDouble()
        val ds = arrayOf(
            this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1),
            this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)
        )
        for (m in noiseSizeY - 1 downTo 0) {
            val f = ds[0][m]
            val g = ds[1][m]
            val h = ds[2][m]
            val n = ds[3][m]
            val o = ds[0][m + 1]
            val p = ds[1][m + 1]
            val q = ds[2][m + 1]
            val r = ds[3][m + 1]
            for (s in verticalNoiseResolution - 1 downTo 0) {
                val t = s.toDouble() / verticalNoiseResolution.toDouble()
                val u = MathHelper.lerp3(t, d, e, f, o, h, q, g, p, n, r)
                val v = m * verticalNoiseResolution + s
                val blockState = getBlockState(u, v)
                if (states != null) {
                    states[v] = blockState
                }
                if (predicate != null && predicate.test(blockState)) {
                    return v + 1
                }
            }
        }
        return 0
    }

    private fun getBlockState(density: Double, y: Int): BlockState {
        return when {
            density > 0.0 -> defaultBlock
            y < seaLevel -> defaultFluid
            else -> AIR
        }
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
                val e = surfaceDepthNoise
                    .sample(
                        o.toDouble() * 0.0625,
                        p.toDouble() * 0.0625,
                        0.0625,
                        m.toDouble() * 0.0625
                    ) * 15.0
                region.getBiome(mutable.set(k + m, q, l + n))
                    .buildSurface(
                        chunkRandom, chunk, o, p, q, e, defaultBlock, defaultFluid,
                        this.seaLevel, region.seed
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
        world: WorldAccess,
        accessor: StructureAccessor,
        chunk: Chunk
    ) {
        val objectList: ObjectList<StructurePiece> = ObjectArrayList(10)
        val objectList2: ObjectList<JigsawJunction> = ObjectArrayList(32)
        val chunkPos = chunk.pos
        val i = chunkPos.x
        val j = chunkPos.z
        val k = i shl 4
        val l = j shl 4
        for (structureFeature in StructureFeature.JIGSAW_STRUCTURES) {
            accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), structureFeature)
                .forEach { start: StructureStart<*>? ->
                    val var6: Iterator<StructurePiece> = start!!.children.iterator()
                    while (true) {
                        var structurePiece: StructurePiece
                        do {
                            if (!var6.hasNext()) {
                                return@forEach
                            }
                            structurePiece = var6.next()
                        } while (!structurePiece.intersectsChunk(chunkPos, 12))
                        if (structurePiece is PoolStructurePiece) {
                            val poolStructurePiece = structurePiece
                            val projection =
                                poolStructurePiece.poolElement.projection
                            if (projection == StructurePool.Projection.RIGID) {
                                objectList.add(poolStructurePiece)
                            }
                            for (jigsawJunction in poolStructurePiece.junctions) {
                                val kx = jigsawJunction.sourceX
                                val lx = jigsawJunction.sourceZ
                                if (kx > k - 12 && lx > l - 12 && kx < k + 15 + 12 && lx < l + 15 + 12) {
                                    objectList2.add(jigsawJunction)
                                }
                            }
                        } else {
                            objectList.add(structurePiece)
                        }
                    }
                }
        }
        val ds = Array(
            2
        ) { Array(noiseSizeZ + 1) { DoubleArray(noiseSizeY + 1) } }
        for (m in 0 until noiseSizeZ + 1) {
            ds[0][m] = DoubleArray(noiseSizeY + 1)
            this.sampleNoiseColumn(ds[0][m], i * noiseSizeX, j * noiseSizeZ + m)
            ds[1][m] = DoubleArray(noiseSizeY + 1)
        }
        val protoChunk = chunk as ProtoChunk
        val heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG)
        val heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG)
        val mutable = BlockPos.Mutable()
        val objectListIterator = objectList.iterator()
        val objectListIterator2 = objectList2.iterator()
        for (n in 0 until noiseSizeX) {
            var p = 0
            while (p < noiseSizeZ + 1) {
                this.sampleNoiseColumn(ds[1][p], i * noiseSizeX + n + 1, j * noiseSizeZ + p)
                ++p
            }
            p = 0
            while (p < noiseSizeZ) {
                var chunkSection = protoChunk.getSection(15)
                chunkSection.lock()
                for (q in noiseSizeY - 1 downTo 0) {
                    val d = ds[0][p][q]
                    val e = ds[0][p + 1][q]
                    val f = ds[1][p][q]
                    val g = ds[1][p + 1][q]
                    val h = ds[0][p][q + 1]
                    val r = ds[0][p + 1][q + 1]
                    val s = ds[1][p][q + 1]
                    val t = ds[1][p + 1][q + 1]
                    for (u in verticalNoiseResolution - 1 downTo 0) {
                        val v = q * verticalNoiseResolution + u
                        val w = v and 15
                        val x = v shr 4
                        if (chunkSection.yOffset shr 4 != x) {
                            chunkSection.unlock()
                            chunkSection = protoChunk.getSection(x)
                            chunkSection.lock()
                        }
                        val y = u.toDouble() / verticalNoiseResolution.toDouble()
                        val z = MathHelper.lerp(y, d, h)
                        val aa = MathHelper.lerp(y, f, s)
                        val ab = MathHelper.lerp(y, e, r)
                        val ac = MathHelper.lerp(y, g, t)
                        for (ad in 0 until horizontalNoiseResolution) {
                            val ae = k + n * horizontalNoiseResolution + ad
                            val af = ae and 15
                            val ag =
                                ad.toDouble() / horizontalNoiseResolution.toDouble()
                            val ah = MathHelper.lerp(ag, z, aa)
                            val ai = MathHelper.lerp(ag, ab, ac)
                            for (aj in 0 until horizontalNoiseResolution) {
                                val ak = l + p * horizontalNoiseResolution + aj
                                val al = ak and 15
                                val am =
                                    aj.toDouble() / horizontalNoiseResolution.toDouble()
                                val an = MathHelper.lerp(am, ah, ai)
                                var ao = MathHelper.clamp(an / 200.0, -1.0, 1.0)
                                var at: Int
                                var au: Int
                                var ar: Int
                                ao = ao / 2.0 - ao * ao * ao / 24.0
                                while (objectListIterator.hasNext()) {
                                    val structurePiece = objectListIterator.next()
                                    val blockBox = structurePiece.boundingBox
                                    at = max(blockBox.minX - ae, ae - blockBox.maxX).coerceAtLeast(0)
                                    au =
                                        v - (blockBox.minY + if (structurePiece is PoolStructurePiece) structurePiece.groundLevelDelta else 0)
                                    ar = max(blockBox.minZ - ak, ak - blockBox.maxZ).coerceAtLeast(0)
                                    ao += method_16572(at, au, ar) * 0.8
                                }
                                objectListIterator.back(objectList.size)
                                while (objectListIterator2.hasNext()) {
                                    val jigsawJunction = objectListIterator2.next()
                                    val `as` = ae - jigsawJunction.sourceX
                                    at = v - jigsawJunction.sourceGroundY
                                    au = ak - jigsawJunction.sourceZ
                                    ao += method_16572(`as`, at, au) * 0.4
                                }
                                objectListIterator2.back(objectList2.size)
                                val blockState = getBlockState(ao, v)
                                if (blockState !== AIR) {
                                    if (blockState.luminance != 0) {
                                        mutable[ae, v] = ak
                                        protoChunk.addLightSource(mutable)
                                    }
                                    chunkSection.setBlockState(af, w, al, blockState, false)
                                    heightmap.trackUpdate(af, v, al, blockState)
                                    heightmap2.trackUpdate(af, v, al, blockState)
                                }
                            }
                        }
                    }
                }
                chunkSection.unlock()
                ++p
            }
            val es = ds[0]
            ds[0] = ds[1]
            ds[1] = es
        }
    }

    override fun getMaxY(): Int {
        return terrainHeight
    }

    override fun getSeaLevel(): Int {
        return settings().seaLevel
    }

    override fun getEntitySpawnList(
        biome: Biome, accessor: StructureAccessor, group: SpawnGroup,
        pos: BlockPos
    ): List<SpawnSettings.SpawnEntry> {
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
        return super.getEntitySpawnList(biome, accessor, group, pos)
    }

    override fun populateEntities(region: ChunkRegion) {
//        if (!this.field_24774.method_28562()) {
        val i = region.centerChunkX
        val j = region.centerChunkZ
        val biome = region.getBiome(ChunkPos(i, j).startPos)
        val chunkRandom = ChunkRandom()
        chunkRandom.setPopulationSeed(region.seed, i shl 4, j shl 4)
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom)
        //        }
    }

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

        private val field_16649 = Util.make(
            FloatArray(13824),
            { array: FloatArray ->
                for (i in 0..23) {
                    for (j in 0..23) {
                        for (k in 0..23) {
                            array[i * 24 * 24 + j * 24 + k] =
                                method_16571(j - 12, k - 12, i - 12).toFloat()
                        }
                    }
                }
            }
        )

        private val field_24775 = Util.make(
            FloatArray(25),
            { fs: FloatArray ->
                for (i in -2..2) {
                    for (j in -2..2) {
                        val f = 10.0f / MathHelper.sqrt((i * i + j * j).toFloat() + 0.2f)
                        fs[i + 2 + (j + 2) * 5] = f
                    }
                }
            }
        )

        private fun method_16572(i: Int, j: Int, k: Int): Double {
            val l = i + 12
            val m = j + 12
            val n = k + 12
            return if (l in 0..23) {
                if (m in 0..23) {
                    if (n in 0..23) field_16649[n * 24 * 24 + l * 24 + m].toDouble() else 0.0
                } else {
                    0.0
                }
            } else {
                0.0
            }
        }

        private fun method_16571(i: Int, j: Int, k: Int): Double {
            val d = i * i + k * k.toDouble()
            val e = j.toDouble() + 0.5
            val f = e * e
            val g = 2.718281828459045.pow(-(f / 16.0 + d / 16.0))
            val h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0
            return h * g
        }

        private val AIR: BlockState = Blocks.AIR.defaultState
    }
}