package com.github.hotm.mixin;

import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Tag.class)
public interface TagAccessor {
    @Accessor
    String getName();

    @Accessor
    TypeTemplate getElement();

    @Accessor
    void setElement(TypeTemplate element);
}
