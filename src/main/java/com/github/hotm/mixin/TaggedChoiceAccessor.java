package com.github.hotm.mixin;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TaggedChoice.class)
public interface TaggedChoiceAccessor<K> {
    @Accessor
    String getName();

    @Accessor
    Type<K> getKeyType();

    @Accessor
    Map<K, TypeTemplate> getTemplates();
}
