package com.github.hotm.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    int getNetherPortalCooldown();

    @Accessor
    void setNetherPortalCooldown(int value);
}
