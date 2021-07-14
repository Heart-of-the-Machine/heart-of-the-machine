package com.github.hotm.mixinapi;

import com.github.hotm.mixin.BuiltinBiomesAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BiomeRegistry {

    /**
     * Registers a biome.
     * <p>
     * This shouldn't be this complicated!
     *
     * @param identifier the identifier of the biome.
     * @param biome      the biome itself.
     * @return a {@code RegistryKey<Biome>} referencing the biome that was registered.
     */
    public static RegistryKey<Biome> register(Identifier identifier, Biome biome) {
        // Another awful hack because the FAPI does not expose anything to do this.
        // This was pretty much copied from InternalBiomeUtils.

        RegistryKey<Biome> key = RegistryKey.of(Registry.BIOME_KEY, identifier);

        Registry.register(BuiltinRegistries.BIOME, identifier, biome);
        int rawId = BuiltinRegistries.BIOME.getRawId(biome);

        Int2ObjectMap<RegistryKey<Biome>> byRawId = BuiltinBiomesAccessor.getBY_RAW_ID();
        if (!byRawId.containsKey(rawId)) {
            byRawId.put(rawId, key);
        }

        return key;
    }
}
