package com.github.hotm.mixin;

import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Tag.class)
public interface TagAccessor {
    @Accessor(remap = false)
    String getName();

    @Accessor(remap = false)
    TypeTemplate getElement();

    @Accessor(remap = false)
    void setElement(TypeTemplate element);
}
