package com.github.hotm.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Invoker to allow creating MultiNoiseBiomeSources.
 */
@Mixin(MultiNoiseBiomeSource.class)
public interface MultiNoiseBiomeSourceInvoker {
    @Invoker("<init>")
    static MultiNoiseBiomeSource create(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> optional) {
        throw new RuntimeException("MultiNoiseBiomeSourceInvoker mixin was not mixed in properly");
    }
}
