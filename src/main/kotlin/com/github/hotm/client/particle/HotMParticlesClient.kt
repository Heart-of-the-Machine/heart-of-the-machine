package com.github.hotm.client.particle

import com.github.hotm.particle.HotMParticles
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry

object HotMParticlesClient {
    fun register() {
        ParticleFactoryRegistry.getInstance().register(HotMParticles.AURA_SIPHON, AuraSiphonParticle::Factory)
        ParticleFactoryRegistry.getInstance().register(HotMParticles.AURA_SOURCE, AuraSourceParticle::Factory)
    }
}