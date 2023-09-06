package com.github.hotm.mod.mixin.api;

import com.github.hotm.mod.mixin.impl.FrustumAccessor;

import org.joml.FrustumIntersection;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Vec3d;

public class HotMClientMixinHelper {
    public static boolean isLineSegmentVisible(Frustum frustum, Vec3d pos1, Vec3d pos2) {
        FrustumAccessor accessor = (FrustumAccessor) frustum;
        double x = accessor.hotm$getX();
        double y = accessor.hotm$getY();
        double z = accessor.hotm$getZ();
        FrustumIntersection intersection = accessor.hotm$getIntersection();

        float x1 = (float) (pos1.x - x);
        float y1 = (float) (pos1.y - y);
        float z1 = (float) (pos1.z - z);
        float x2 = (float) (pos2.x - x);
        float y2 = (float) (pos2.y - y);
        float z2 = (float) (pos2.z - z);

        return intersection.testLineSegment(x1, y1, z1, x2, y2, z2);
    }
}
