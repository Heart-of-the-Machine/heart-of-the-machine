package com.github.hotm.client.render.blockentity

import com.github.hotm.icon.HotMIcons
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.RenderedDependableAuraNode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*
import kotlin.math.PI
import kotlin.math.atan2

open class SimpleDependableAuraNodeBlockEntityRenderer<T : BlockEntity>(protected val ctx: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<T> {
    override fun rendersOutsideBoundingBox(blockEntity: T): Boolean {
        return true
    }

    override fun getRenderDistance(): Int {
        return 256
    }

    override fun render(
        entity: T,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val world = entity.world ?: return
        val pos = entity.pos
        val access = StorageUtils.getAuraNetAccess(world)
        val node = access[pos] as? RenderedDependableAuraNode ?: return
        val children = node.getChildrenForRender()

        for (childPos in children) {
            val offset = childPos.subtract(pos)

            if (offset == pos) {
                return
            }

            matrices.push()

            matrices.translate(0.5, 0.5, 0.5)

            renderBeam(matrices, vertexConsumers, offset, world.time, tickDelta, 0.0625f, 0.125f)

            matrices.pop()
        }
    }

    protected open fun renderBeam(
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        offset: Vec3i,
        worldTime: Long,
        tickDelta: Float,
        innerRadius: Float,
        outerRadius: Float,
    ) {
        val dx = offset.x.toFloat()
        val dy = offset.y.toFloat()
        val dz = offset.z.toFloat()

        val xzLen = MathHelper.sqrt(dx * dx + dz * dz)
        val len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz)

        val animationAmount = Math.floorMod(worldTime, 40).toFloat() + tickDelta
        // In this case, negative texture shift gives the appearance of the texture moving upward
        val textureShift = MathHelper.fractionalPart(-animationAmount * 0.2f)
        val pitchShift = -atan2(xzLen, dy)
        val yawShift = -atan2(dz, dx) - PI.toFloat() / 2f
        val rollShift = animationAmount * 2.25f - 45.0f
        val v2 = -1f + textureShift

        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(yawShift))
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(pitchShift))

        matrices.push()
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rollShift))
        renderBeamCross(
            matrices,
            consumers.getBuffer(RenderLayer.getBeaconBeam(HotMIcons.AURA_NODE_BEAM, true)),
            1f,
            len,
            innerRadius,
            0.0f,
            1.0f,
            len * (0.5f / innerRadius) + v2,
            v2
        )
        matrices.pop()

        matrices.push()
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-rollShift))
        renderBeamSquare(
            matrices,
            consumers.getBuffer(RenderLayer.getBeaconBeam(HotMIcons.AURA_NODE_BEAM, true)),
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

    protected open fun renderBeamSquare(
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

    protected open fun renderBeamCross(
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