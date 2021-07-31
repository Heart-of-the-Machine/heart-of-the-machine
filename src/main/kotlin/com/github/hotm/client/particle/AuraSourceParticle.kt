package com.github.hotm.client.particle

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType

class AuraSourceParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    spriteProvider: FabricSpriteProvider
) : SpriteBillboardParticle(world, x, y, z) {

    init {
        setSprite(spriteProvider)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    class Factory(private val spriteProvider: FabricSpriteProvider): ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            parameters: DefaultParticleType,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            return AuraSourceParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider)
        }
    }
}