package com.github.hotm.mod.world.gen.carver

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.carver.CaveCarverConfig

object HotMCarvers {
    fun init() {
        Registry.register(Registries.CARVER, id("air_only_cave"), AirOnlyCaveCarver(CaveCarverConfig.CODEC))
    }
}
