package com.github.hotm.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.decorator.DecoratorContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DecoratorContext.class)
public interface DecoratorContextAccessor {
    @Accessor
    StructureWorldAccess getWorld();
}
