package com.github.hotm.misc

import net.minecraft.state.property.IntProperty

object HotMProperties {
    const val MAX_DISTANCE = 16
    val DISTANCE: IntProperty by lazy { IntProperty.of("distance", 1, MAX_DISTANCE) }
}