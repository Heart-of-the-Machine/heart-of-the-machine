package com.github.hotm.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinBiomes.class)
public interface BuiltinBiomesAccessor {
    @Accessor("BY_RAW_ID")
    static Int2ObjectMap<RegistryKey<Biome>> getByRawId() {
        throw new RuntimeException("BuiltinBiomesAccessor mixin was not mixed in properly!");
    }
}
