package com.github.hotm

import net.minecraft.util.Identifier
import net.minecraft.world.biome.source.BiomeAccessType
import net.minecraft.world.dimension.DimensionType
import java.util.*

/**
 * Instantiable version of DimensionType.
 */
class HotMDimensionType(
    fixedTime: OptionalLong,
    hasSkyLight: Boolean,
    hasCeiling: Boolean,
    ultrawarm: Boolean,
    natural: Boolean,
    shrunk: Boolean,
    hasEnderDragonFight: Boolean,
    piglinSafe: Boolean,
    bedWorks: Boolean,
    respawnAnchorWorks: Boolean,
    hasRaids: Boolean,
    logicalHeight: Int,
    biomeAccessType: BiomeAccessType,
    infiniburn: Identifier,
    ambientLight: Float
) : DimensionType(
    fixedTime,
    hasSkyLight,
    hasCeiling,
    ultrawarm,
    natural,
    shrunk,
    hasEnderDragonFight,
    piglinSafe,
    bedWorks,
    respawnAnchorWorks,
    hasRaids,
    logicalHeight,
    biomeAccessType,
    infiniburn,
    ambientLight
)
