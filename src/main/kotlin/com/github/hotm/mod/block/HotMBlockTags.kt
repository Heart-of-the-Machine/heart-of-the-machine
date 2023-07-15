package com.github.hotm.mod.block

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object HotMBlockTags {
    val LEYLINES = TagKey.of(RegistryKeys.BLOCK, id("leylines"))
    val NECTERE_CARVER_REPLACABLES = TagKey.of(RegistryKeys.BLOCK, id("nectere_carver_replaceables"))
    val PLASSEIN_FERTILE = TagKey.of(RegistryKeys.BLOCK, id("plassein_fertile"))
    val PLASSEIN_SOURCE = TagKey.of(RegistryKeys.BLOCK, id("plassein_source"))
}
