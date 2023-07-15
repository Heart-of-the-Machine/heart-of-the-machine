package com.github.hotm.mod.util

import org.quiltmc.qkl.library.math.minus
import net.minecraft.util.math.Vec3d

object GeometryUtils {
    fun inEllipsoid(center: Vec3d, radius: Vec3d, pos: Vec3d): Boolean {
        val nPos = pos.minus(center)
        val scaled = nPos.multiply(1.0 / radius.x, 1.0 / radius.y, 1.0 / radius.z)
        return scaled.lengthSquared() <= 1.0
    }
}
