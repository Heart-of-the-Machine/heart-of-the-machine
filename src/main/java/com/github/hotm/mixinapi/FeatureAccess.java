package com.github.hotm.mixinapi;

import com.github.hotm.mixin.ConfiguredFeaturesDecoratorsAccessor;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class FeatureAccess {
    public static ConfiguredDecorator<?> getSquareHeightmap() {
        return ConfiguredFeaturesDecoratorsAccessor.getSQUARE_HEIGHTMAP();
    }
}
