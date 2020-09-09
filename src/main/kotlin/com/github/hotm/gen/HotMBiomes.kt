package com.github.hotm.gen

import com.github.hotm.HotMConstants
import com.github.hotm.gen.biome.NectereBiomeData
import com.github.hotm.gen.feature.HotMBiomeFeatures
import com.github.hotm.gen.feature.HotMStructureFeatures
import com.github.hotm.mixin.BuiltinBiomesAccessor
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.biome.SpawnSettings

/**
 * Registers biomes for the Nectere dimension.
 */
object HotMBiomes {
    private val BIOME_KEYS = mutableMapOf<Identifier, RegistryKey<Biome>>()
    private val BIOME_DATA = mutableMapOf<RegistryKey<Biome>, NectereBiomeData>()
    private val BIOME_NOISE = mutableMapOf<RegistryKey<Biome>, Biome.MixedNoisePoint>()
    private val BIOME_DEFAULTS = mutableMapOf<RegistryKey<Biome>, Biome>()

    /**
     * The thinking forest biome.
     */
    val THINKING_FOREST = register(
        "thinking_forest",
        createThinkingForest(),
        8.0,
        World.OVERWORLD,
        true,
        Biome.MixedNoisePoint(0.25f, 0.25f, 0.25f, 0.0f, 1.0f)
    )

    /**
     * The wasteland biome.
     */
    val WASTELAND = register(
        "wasteland",
        createWasteland(),
        1.0,
        World.OVERWORLD,
        true,
        Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 1.0f)
    )

    /**
     * Makes sure everything is loaded and registered.
     */
    fun register() {}

    /**
     * Gets the builtin set of biomes by instantiating a new DynamicRegistryManager and getting the biome Registry from
     * there.
     */
    fun builtinBiomeRegistry(): Registry<Biome> {
        return BuiltinRegistries.BIOME
    }

    /**
     * Gets all the Nectere biomes.
     */
    fun biomes(): Map<Identifier, RegistryKey<Biome>> {
        return BIOME_KEYS
    }

    /**
     * Gets all Nectere biome data.
     */
    fun biomeData(): Map<RegistryKey<Biome>, NectereBiomeData> {
        return BIOME_DATA
    }

    /**
     * Gets the MixedNoisePoints for all Nectere biomes.
     */
    fun biomeNoise(): Map<RegistryKey<Biome>, Biome.MixedNoisePoint> {
        return BIOME_NOISE
    }

    private fun createThinkingForest(): Biome {
        val effects = BiomeEffects.Builder()
        effects.waterColor(0x3f76e4)
        effects.waterFogColor(0x050533)
        effects.fogColor(0x7591c7)
        effects.moodSound(BiomeMoodSound.CAVE)
        effects.skyColor(getSkyColor(0.5f))
        effects.grassColor(0x00c9db)
        effects.foliageColor(0x00a9b8)

        val spawns = SpawnSettings.Builder()

        val gen = GenerationSettings.Builder()
        gen.surfaceBuilder(HotMSurfaceBuilders.THINKING_FOREST)
        setupDefaultGen(gen)
        HotMBiomeFeatures.addRefusePiles(gen)
        HotMBiomeFeatures.addPlasseinGrowths(gen)
        HotMBiomeFeatures.addPlasseinSurfaceTrees(gen)
        HotMBiomeFeatures.addServerTowers(gen)
        HotMBiomeFeatures.addTransmissionTowers(gen)

        val biome = Biome.Builder()
        biome.precipitation(Biome.Precipitation.RAIN)
        biome.category(Biome.Category.JUNGLE)
        biome.depth(0.45F).scale(0.3F)
        biome.temperature(0.5f).downfall(0.5f)
        biome.effects(effects.build())
        biome.spawnSettings(spawns.build())
        biome.generationSettings(gen.build())

        return biome.build()
    }

    private fun createWasteland(): Biome {
        val effects = BiomeEffects.Builder()
        effects.waterColor(0x7591c7)
        effects.waterFogColor(0x050533)
        effects.fogColor(0x222222)
        effects.moodSound(BiomeMoodSound.CAVE)
        effects.skyColor(getSkyColor(0.8f))
        effects.grassColor(0x7cdb00)
        effects.foliageColor(0x62ad00)

        val spawns = SpawnSettings.Builder()

        val gen = GenerationSettings.Builder()
        gen.surfaceBuilder(HotMSurfaceBuilders.WASTELAND)
        setupDefaultGen(gen)
        HotMBiomeFeatures.addRefusePiles(gen)

        val biome = Biome.Builder()
        biome.precipitation(Biome.Precipitation.NONE)
        biome.category(Biome.Category.PLAINS)
        biome.depth(0.125F).scale(0.05F)
        biome.temperature(0.8F).downfall(0.0F)
        biome.effects(effects.build())
        biome.spawnSettings(spawns.build())
        biome.generationSettings(gen.build())

        return biome.build()
    }

    private fun setupDefaultGen(gen: GenerationSettings.Builder) {
        gen.structureFeature(HotMStructureFeatures.NECTERE_SIDE_NECTERE_PORTAL)
        HotMBiomeFeatures.addCrystalGrowths(gen)
    }

    private fun register(
        name: String,
        biome: Biome,
        coordinateMultiplier: Double,
        targetWorld: RegistryKey<World>,
        isPortalable: Boolean,
        biomeNoise: Biome.MixedNoisePoint
    ): RegistryKey<Biome> {
        val ident = HotMConstants.identifier(name)
        val key = RegistryKey.of(Registry.BIOME_KEY, ident)

        BIOME_KEYS[ident] = key
        BIOME_DATA[key] = NectereBiomeData(key, coordinateMultiplier, targetWorld, isPortalable)
        BIOME_NOISE[key] = biomeNoise
        BIOME_DEFAULTS[key] = biome

        val byRawId = BuiltinBiomesAccessor.getByRawId()
        val rawId = byRawId.size

        byRawId[rawId] = key
        BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, key, biome)
//        Registry.register(BuiltinRegistries.BIOME, ident, biome)

        return key
    }

    private fun getSkyColor(temperature: Float): Int {
        var f = temperature / 3.0f
        f = MathHelper.clamp(f, -1.0f, 1.0f)
        return MathHelper.hsvToRgb(0.62222224f - f * 0.05f, 0.5f + f * 0.1f, 1.0f)
    }
}