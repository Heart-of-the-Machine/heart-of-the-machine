package com.github.hotm.mixin;

import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/world/gen/feature/ConfiguredFeatures$Decorators")
public interface ConfiguredFeaturesDecoratorsAccessor {
    @Accessor("SQUARE_HEIGHTMAP")
    static ConfiguredDecorator<?> getSQUARE_HEIGHTMAP() {
        throw new IllegalStateException("ConfiguredFeaturesDecoratorsAccessor mixin error");
    }
}
