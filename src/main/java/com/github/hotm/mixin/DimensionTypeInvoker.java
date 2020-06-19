package com.github.hotm.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.OptionalLong;

/**
 * Allows me to construct DimensionType objects.
 */
@Mixin(DimensionType.class)
public interface DimensionTypeInvoker {
    @Invoker("<init>")
    static DimensionType create(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm,
                                boolean natural, boolean shrunk, boolean hasEnderDragonFight, boolean piglinSafe,
                                boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight,
                                BiomeAccessType biomeAccessType, Identifier infiniburn, float ambientLight) {
        throw new RuntimeException("DimensionTypeInvoker mixin was not properly mixed in!");
    }
}
