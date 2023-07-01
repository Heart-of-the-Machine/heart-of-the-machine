package com.github.hotm.mod.world.biome

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.biome.Biome

object HotMBiomeTags {
    val HAS_NECTERE_PORTAL: TagKey<Biome> = TagKey.of(RegistryKeys.BIOME, id("has_nectere_portal"))
}
