package com.github.hotm.mixin;

import com.mojang.datafixers.types.templates.Product;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Product.class)
public interface ProductAccessor {
    @Accessor
    TypeTemplate getF();

    @Accessor
    TypeTemplate getG();
}
