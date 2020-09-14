package com.github.hotm

import net.minecraft.state.property.IntProperty

object HotMProperties {
    const val MAX_DISTANCE = 16
    val DISTANCE: IntProperty = IntProperty.of("distance", 1, MAX_DISTANCE)
}