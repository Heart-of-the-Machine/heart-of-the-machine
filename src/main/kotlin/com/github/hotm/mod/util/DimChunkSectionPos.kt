package com.github.hotm.mod.util

import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World

data class DimChunkSectionPos(val dim: RegistryKey<World>, val pos: ChunkSectionPos)
