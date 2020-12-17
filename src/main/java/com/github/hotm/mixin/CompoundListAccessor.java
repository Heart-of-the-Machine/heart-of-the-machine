package com.github.hotm.mixin;

import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CompoundList.class)
public interface CompoundListAccessor {
    @Accessor
    TypeTemplate getKey();

    @Accessor
    TypeTemplate getElement();
}
