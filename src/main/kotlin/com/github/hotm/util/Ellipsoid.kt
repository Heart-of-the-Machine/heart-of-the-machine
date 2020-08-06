package com.github.hotm.util

import net.minecraft.util.math.Vec3i

/**
 * Represents an ellipsoid.
 */
class Ellipsoid(val x: Double, val y: Double, val z: Double, val width: Double, val height: Double, val depth: Double) {
    constructor(pos: Vec3i, width: Double, height: Double, depth: Double) : this(
        pos.x.toDouble(),
        pos.y.toDouble(),
        pos.z.toDouble(),
        width,
        height,
        depth
    )

    fun isPosWithin(pos: Vec3i): Boolean {
        val xPart = (pos.x - x) * 2 / width
        val yPart = (pos.y - y) * 2 / height
        val zPart = (pos.z - z) * 2 / depth

        return xPart * xPart + yPart * yPart + zPart * zPart <= 1
    }
}