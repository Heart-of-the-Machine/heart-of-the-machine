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

    private val endX = x
    private val endY = y
    private val endZ = z

    init {
        maxAge = (Math.random() * 10.0).toInt() + 40
        scale = 0.1f * (random.nextFloat() * 0.2f + 0.5f)

        this.x = x
        this.y = y
        this.z = z

        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ

        red = 0.8f + random.nextFloat() * 0.2f
        green = 0.8f + random.nextFloat() * 0.2f
        blue = 0.8f + random.nextFloat() * 0.2f

        setSprite(spriteProvider)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    override fun move(dx: Double, dy: Double, dz: Double) {
        boundingBox = boundingBox.offset(dx, dy, dz)
        repositionFromBoundingBox()
    }

    override fun getSize(tickDelta: Float): Float {
        var factor = (age.toFloat() + tickDelta) / maxAge.toFloat()
        factor *= factor
        factor = 1.0f - factor
        return scale * factor
    }

    override fun getBrightness(tint: Float): Int {
        val brightness = super.getBrightness(tint)
        return brightness or 255
    }

    override fun tick() {
        prevPosX = x
        prevPosY = y
        prevPosZ = z

        if (age++ >= maxAge) {
            markDead()
        } else {
            var factor = age.toFloat() / maxAge.toFloat()
            factor = 1.0f - factor
            val yFactor = factor
            factor = -factor + factor * factor * 2.0f
            factor = 1.0f - factor
            x = endX + velocityX * factor.toDouble()
            y = endY + velocityY * factor.toDouble() + (1.0f - yFactor).toDouble()
            z = endZ + velocityZ * factor.toDouble()
        }
    }

    class Factory(private val spriteProvider: FabricSpriteProvider) : ParticleFactory<DefaultParticleType> {
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
