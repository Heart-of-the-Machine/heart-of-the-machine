package com.github.hotm.misc

import com.github.hotm.HotMConstants
import net.minecraft.block.Block
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry

object HotMBlockTags {
    lateinit var LEYLINES: TagKey<Block>
        private set
    lateinit var PLASSEIN_FERTILE: TagKey<Block>
        private set
    lateinit var PLASSEIN_SOURCE: TagKey<Block>
        private set
    lateinit var SCAFFOLDING: TagKey<Block>
        private set

    fun register() {
        LEYLINES = TagKey.of(Registry.BLOCK_KEY, HotMConstants.identifier("leylines"))
        PLASSEIN_FERTILE = TagKey.of(Registry.BLOCK_KEY, HotMConstants.identifier("plassein_fertile"))
        PLASSEIN_SOURCE = TagKey.of(Registry.BLOCK_KEY, HotMConstants.identifier("plassein_source"))
        SCAFFOLDING = TagKey.of(Registry.BLOCK_KEY, HotMConstants.identifier("scaffolding"))
    }
}
