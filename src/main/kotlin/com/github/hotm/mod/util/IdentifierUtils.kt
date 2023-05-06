package com.github.hotm.mod.util

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

fun Identifier.asBiomeKey(): RegistryKey<Biome> = RegistryKey.of(RegistryKeys.BIOME, this)
