package com.github.hotm.particle

import com.github.hotm.HotMConstants.identifier
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.registry.Registry

object HotMParticles {
    lateinit var AURA_SIPHON: DefaultParticleType
        private set
    lateinit var AURA_SOURCE: DefaultParticleType
        private set

    fun register() {
        AURA_SIPHON = FabricParticleTypes.simple()
        AURA_SOURCE = FabricParticleTypes.simple()

        Registry.register(Registry.PARTICLE_TYPE, identifier("aura_siphon"), AURA_SIPHON)
        Registry.register(Registry.PARTICLE_TYPE, identifier("aura_source"), AURA_SOURCE)
    }
}