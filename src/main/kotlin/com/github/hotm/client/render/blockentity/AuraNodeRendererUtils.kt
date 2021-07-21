package com.github.hotm.client.render.blockentity

import com.github.hotm.client.render.HotMRenderMaterials
import com.github.hotm.icon.HotMIcons
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3f
import kotlin.math.PI
import kotlin.math.atan2

object AuraNodeRendererUtils {
    fun renderBeam(
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        dx: Float,
        dy: Float,
        dz: Float,
        worldTime: Long,
        tickDelta: Float,
        innerRadius: Float,
        outerRadius: Float,
    ) {
        val xzLen = MathHelper.sqrt(dx * dx + dz * dz)
        val len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz)

        val animationAmount = Math.floorMod(worldTime, 40).toFloat() + tickDelta
        // In this case, negative texture shift gives the appearance of the texture moving upward
        val textureShift = MathHelper.fractionalPart(-animationAmount * 0.2f)
        val pitchShift = -atan2(xzLen, dy)
        val yawShift = -atan2(dz, dx) - PI.toFloat() / 2f
        val rollShift = animationAmount * 2.25f - 45.0f
        val v2 = -1f + textureShift

        matrices.push()

        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(yawShift))
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(pitchShift))

        matrices.push()
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rollShift))
        renderBeamCross(
            matrices,
            HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMIcons.AURA_NODE_BEAM, false),
            1f,
            len,
            innerRadius,
            0.0f,
            1.0f,
            len * (0.5f / innerRadius) + v2,
            v2
        )
        matrices.pop()

        if (HotMRenderMaterials.shouldRenderOuterAuraNodeBeam()) {
            matrices.push()
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-rollShift))
            renderBeamSquare(
                matrices,
                HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMIcons.AURA_NODE_BEAM, true),
                0.125f,
                len,
                outerRadius,
                0.0f,
                1.0f,
                len * (0.5f / outerRadius) + v2,
                v2
            )
            matrices.pop()
        }

        matrices.pop()
    }

    fun renderBeamCross(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        alpha: Float,
        height: Float,
        radius: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float
    ) {
        val entry = matrices.peek()
        val modelMat = entry.model
        val normalMat = entry.normal
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, 0f, radius, 0f, -radius, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, 0f, -radius, 0f, radius, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, radius, 0f, -radius, 0f, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, -radius, 0f, radius, 0f, u1, u2, v1, v2)
    }

    fun renderBeamSquare(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        alpha: Float,
        height: Float,
        radius: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float
    ) {
        val entry = matrices.peek()
        val modelMat = entry.model
        val normalMat = entry.normal
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, 0f, radius, radius, 0f, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, 0f, -radius, -radius, 0f, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, radius, 0f, 0f, -radius, u1, u2, v1, v2)
        renderBeamFace(modelMat, normalMat, vertices, alpha, height, -radius, 0f, 0f, radius, u1, u2, v1, v2)
    }

    private fun renderBeamFace(
        modelMatrix: Matrix4f,
        normalMatrix: Matrix3f,
        vertices: VertexConsumer,
        alpha: Float,
        height: Float,
        x1: Float,
        z1: Float,
        x2: Float,
        z2: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float
    ) {
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, height, x1, z1, u2, v1)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, 0f, x1, z1, u2, v2)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, 0f, x2, z2, u1, v2)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, height, x2, z2, u1, v1)
    }

    private fun renderBeamVertex(
        modelMatrix: Matrix4f,
        normalMatrix: Matrix3f,
        vertices: VertexConsumer,
        alpha: Float,
        y: Float,
        x: Float,
        z: Float,
        u: Float,
        v: Float
    ) {
        vertices.vertex(modelMatrix, x, y, z).color(1f, 1f, 1f, alpha).texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next()
    }
}
