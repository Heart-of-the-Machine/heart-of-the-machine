package com.github.hotm.mod.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import org.joml.FrustumIntersection;

import net.minecraft.client.render.Frustum;

@Mixin(Frustum.class)
public interface FrustumAccessor {
    @Accessor("x")
    double hotm$getX();

    @Accessor("y")
    double hotm$getY();

    @Accessor("z")
    double hotm$getZ();

    @Accessor("intersection")
    FrustumIntersection hotm$getIntersection();
}
