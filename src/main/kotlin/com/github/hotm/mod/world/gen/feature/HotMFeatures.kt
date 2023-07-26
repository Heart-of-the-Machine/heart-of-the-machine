package com.github.hotm.mod.world.gen.feature

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.world.gen.feature.tree.SolarArrayFoliagePlacer
import com.github.hotm.mod.world.gen.feature.tree.SolarArrayTrunkPlacer
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.foliage.FoliagePlacerType
import net.minecraft.world.gen.trunk.TrunkPlacerType

object HotMFeatures {
    val CRYSTAL_GROWTH by lazy { CrystalGrowthFeature(CrystalGrowthConfig.CODEC) }
    val SERVER_TOWER by lazy { ServerTowerFeature(ServerTowerConfig.CODEC) }

    // nectere portal stuff

    val NON_NECTERE_SIDE_NECTERE_PORTAL by lazy { NecterePortalFeature(DefaultFeatureConfig.CODEC) }
    val NON_NECTERE_SIDE_NECTERE_PORTAL_ID = id("nns_nectere_portal")
    val NON_NECTERE_SIDE_NECTERE_PORTAL_KEY: RegistryKey<PlacedFeature> =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, NON_NECTERE_SIDE_NECTERE_PORTAL_ID)

    // tree stuff

    val SOLAR_ARRAY_TRUNK_PLACER = TrunkPlacerType(SolarArrayTrunkPlacer.CODEC)
    val SOLAR_ARRAY_FOLIAGE_PLACER = FoliagePlacerType(SolarArrayFoliagePlacer.CODEC)
    val SOLAR_ARRAY_CONFIGURED_FEATURE = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id("solar_array"))

    fun init() {
        Registry.register(Registries.FEATURE, id("crystal_growth"), CRYSTAL_GROWTH)
        Registry.register(Registries.FEATURE, id("server_tower"), SERVER_TOWER)

        Registry.register(Registries.FEATURE, NON_NECTERE_SIDE_NECTERE_PORTAL_ID, NON_NECTERE_SIDE_NECTERE_PORTAL)

        BiomeModifications.addFeature(
            BiomeSelectors.all(),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            NON_NECTERE_SIDE_NECTERE_PORTAL_KEY
        )

        Registry.register(Registries.TRUNK_PLACER_TYPE, id("solar_array_trunk_placer"), SOLAR_ARRAY_TRUNK_PLACER)
        Registry.register(Registries.FOLIAGE_PLACER_TYPE, id("solar_array_foliage_placer"), SOLAR_ARRAY_FOLIAGE_PLACER)
    }
}
