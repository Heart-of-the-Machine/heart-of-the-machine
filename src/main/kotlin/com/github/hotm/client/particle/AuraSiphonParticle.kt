package com.github.hotm.client.particle

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.MathHelper
import kotlin.math.sqrt

class AuraSiphonParticle(
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

    private val length: Double

    init {
        maxAge = (Math.random() * 10.0).toInt() + 40

        val startX = x + maxAge * velocityX
        val startY = y + maxAge * velocityY
        val startZ = z + maxAge * velocityZ

        val dx = endX - startX
        val dy = endY - startY
        val dz = endZ - startZ

        length = sqrt(dx * dx + dy * dy + dz * dz)

        setPos(startX, startY, startZ)
        prevPosX = startX
        prevPosY = startY
        prevPosZ = startZ

        this.velocityX = random.nextDouble() * 0.1
        this.velocityY = random.nextDouble() * 0.1
        this.velocityZ = random.nextDouble() * 0.1

        colorRed = 0.9f + random.nextFloat() * 0.1f
        colorGreen = 0.9f + random.nextFloat() * 0.1f
        colorBlue = 0.9f + random.nextFloat() * 0.1f

        setSprite(spriteProvider)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    override fun move(dx: Double, dy: Double, dz: Double) {
        boundingBox = boundingBox.offset(dx, dy, dz)
        repositionFromBoundingBox()
    }

    override fun getSize(tickDelta: Float): Float {
        var factor = (age.toFloat() + tickDelta) / maxAge.toFloat()
        factor = 1.0f - factor
        factor *= factor
        factor = 1.0f - factor
        return scale * factor
    }

    override fun getBrightness(tint: Float): Int {
        val brightness = super.getBrightness(tint)
        var factor = age.toFloat() / maxAge.toFloat()
        factor *= factor
        factor *= factor
        val blockLight = brightness and 255
        var skyLight = brightness shr 16 and 255
        skyLight += (factor * 15.0f * 16.0f).toInt()
        if (skyLight > 240) {
            skyLight = 240
        }
        return blockLight or skyLight shl 16
    }

    override fun tick() {
        prevPosX = x
        prevPosY = y
        prevPosZ = z

        if (age++ >= maxAge) {
            markDead()
        } else {
            var ax = endX - x
            var ay = endY - y
            var az = endZ - z

            var scale = ax * ax + ay * ay + az * az
            if (scale > 0.0001) {
                move(velocityX, velocityY, velocityZ)

                scale = MathHelper.fastInverseSqrt(scale) / length
                ax *= scale
                ay *= scale
                az *= scale

                velocityX += ax
                velocityY += ay
                velocityZ += az
            }
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
            return AuraSiphonParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider)
        }
    }
}