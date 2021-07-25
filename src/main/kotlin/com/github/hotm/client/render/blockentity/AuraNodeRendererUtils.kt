package com.github.hotm.client.render.blockentity

import com.github.hotm.client.blockmodel.HotMBlockModels
import com.github.hotm.client.render.HotMRenderMaterials
import com.github.hotm.icon.HotMIcons
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*
import net.minecraft.world.BlockRenderView
import java.util.*
import kotlin.math.PI
import kotlin.math.atan2

object AuraNodeRendererUtils {
    fun renderBeam(
        world: BlockRenderView,
        blockstate: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        dx: Float,
        dy: Float,
        dz: Float,
        worldTime: Long,
        tickDelta: Float,
        overlay: Int,
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

        matrices.push()
        matrices.translate(0.0, 0.35, 0.0)
        renderCrown(world, blockstate, pos, matrices, consumers, overlay, 4, 0.15, animationAmount * 18f, 0.75f)
        matrices.pop()

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

    fun renderCrown(
        world: BlockRenderView,
        blockstate: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        overlay: Int,
        numPieces: Int,
        crownRadius: Double,
        crownRoll: Float,
        scale: Float
    ) {
        val increment = 360f / numPieces.toFloat()
        for (i in 0 until numPieces) {
            matrices.push()
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i.toFloat() * increment + crownRoll))
            matrices.translate(0.0, 0.0, -crownRadius)
            matrices.scale(scale, scale, scale)
            renderCrownPiece(world, blockstate, pos, matrices, consumers, overlay)
            matrices.pop()
        }
    }

    private fun renderCrownPiece(
        world: BlockRenderView,
        blockstate: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        overlay: Int
    ) {
        matrices.push()
        val blockRenderManager: BlockRenderManager = MinecraftClient.getInstance().blockRenderManager
        val bakedModelManager = blockRenderManager.models.modelManager
        matrices.translate(-0.5, 0.0, -0.5)
        blockRenderManager.modelRenderer.render(
            world,
            bakedModelManager.getModel(HotMBlockModels.AURA_NODE_BEAM_CROWN_PIECE),
            blockstate,
            pos,
            matrices,
            consumers.getBuffer(TexturedRenderLayers.getEntitySolid()),
            false,
            Random(42),
            42L,
            overlay
        )
        matrices.pop()
    }
}
