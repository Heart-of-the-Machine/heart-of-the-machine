package com.github.hotm.misc

import com.github.hotm.HotMConstants
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.tag.Tag

object HotMBlockTags {
    lateinit var LEYLINES: Tag<Block>
        private set
    lateinit var PLASSEIN_FERTILE: Tag<Block>
        private set
    lateinit var PLASSEIN_SOURCE: Tag<Block>
        private set
    lateinit var SCAFFOLDING: Tag<Block>
        private set

    fun register() {
        LEYLINES = TagRegistry.block(HotMConstants.identifier("leylines"))
        PLASSEIN_FERTILE = TagRegistry.block(HotMConstants.identifier("plassein_fertile"))
        PLASSEIN_SOURCE = TagRegistry.block(HotMConstants.identifier("plassein_source"))
        SCAFFOLDING = TagRegistry.block(HotMConstants.identifier("scaffolding"))
    }
}