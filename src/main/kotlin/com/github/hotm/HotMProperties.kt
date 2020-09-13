package com.github.hotm

import com.github.hotm.blocks.PlasseinLeavesBlock
import net.minecraft.state.property.IntProperty

object HotMProperties {
    val DISTANCE = IntProperty.of("distance", 1, PlasseinLeavesBlock.MAX_DISTANCE)
}