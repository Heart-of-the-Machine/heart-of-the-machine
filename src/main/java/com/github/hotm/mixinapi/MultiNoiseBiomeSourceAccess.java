package com.github.hotm.mixinapi;

import com.github.hotm.mixin.MultiNoiseBiomeSourceAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MultiNoiseBiomeSourceAccess {
    public static MultiNoiseBiomeSource create(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list,
                                               Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional) {
        return MultiNoiseBiomeSourceAccessor.create(seed, list, optional);
    }
}
