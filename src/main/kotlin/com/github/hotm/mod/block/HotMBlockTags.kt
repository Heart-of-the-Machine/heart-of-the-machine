package com.github.hotm.mod.block

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object HotMBlockTags {
    val NECTERE_CARVER_REPLACABLES = TagKey.of(RegistryKeys.BLOCK, id("nectere_carver_replaceables"))
    val PLASSEIN_SOURCE = TagKey.of(RegistryKeys.BLOCK, id("plassein_source"))
}
