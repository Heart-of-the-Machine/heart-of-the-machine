package com.github.hotm.client.render.blockentity

import java.util.*
import com.github.hotm.client.HotMSprites
import com.github.hotm.client.blockmodel.HotMBlockModels
import com.github.hotm.client.render.HotMRenderMaterials
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.exp
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Quaternionf
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.BlockRenderView
import net.minecraft.world.World

object AuraNodeRendererUtils {
    fun renderBeam(
        world: World,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        tickDelta: Float,
        offset: Vec3i,
        energy: Float,
        crownRoll: Float
    ) {
        renderBeam(
            world,
            world.getBlockState(pos),
            pos,
            matrices,
            consumers,
            offset.x.toFloat(),
            offset.y.toFloat(),
            offset.z.toFloat(),
            world.time,
            tickDelta,
            crownRoll,
            display(energy, 0.03125f, 0.25f, 0.01f),
            display(energy, 0.0625f, 0.28125f, 0.01f),
            display(energy, 0.125f, 0.25f, 0.01f)
        )
    }

    private fun display(energy: Float, min: Float, max: Float, rate: Float): Float {
        return logistic((max - min) * 2f, rate, 0f, energy) - max + 2f * min
    }

    private fun logistic(max: Float, rate: Float, mid: Float, x: Float): Float {
        return max / (1 + exp(-rate * (x - mid)))
    }

    fun renderBeam(
        world: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        dx: Float,
        dy: Float,
        dz: Float,
        worldTime: Long,
        tickDelta: Float,
        crownRoll: Float,
        innerRadius: Float,
        outerRadius: Float,
        crownRadius: Float,
    ) {
        val xzLen = MathHelper.sqrt(dx * dx + dz * dz)
        val len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz)

        val animationAmount = Math.floorMod(worldTime, 40).toFloat() + tickDelta
        val pitchShift = -atan2(xzLen, dy)
        val yawShift = -atan2(dz, dx) - PI.toFloat() / 2f
        val rollShift = animationAmount * 2.25f - 45.0f

        matrices.push()

        matrices.multiply(Quaternionf().rotationY(yawShift))
        matrices.multiply(Quaternionf().rotationX(pitchShift))

        matrices.push()
        matrices.multiply(Quaternionf().rotationY(rollShift))
        renderBeamSquare(
            matrices,
            HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMSprites.AURA_NODE_BEAM, false),
            1f,
            len,
            innerRadius,
            0.0f,
            1.0f,
            0.0f,
            1.0f
        )
        renderBeamEnds(
            matrices,
            HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMSprites.AURA_NODE_BEAM_END, false),
            1f,
            len,
            innerRadius,
            0.0f,
            1.0f,
            0.0f,
            1.0f
        )
        matrices.pop()

        if (HotMRenderMaterials.shouldRenderOuterAuraNodeBeam()) {
            matrices.push()
            matrices.multiply(Quaternionf().rotationY(rollShift))
            matrices.translate(0.0, -1.0 / 32.0, 0.0)
            renderBeamSquare(
                matrices,
                HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMSprites.AURA_NODE_BEAM, true),
                0.125f,
                len + 2f / 32f,
                outerRadius,
                0.0f,
                1.0f,
                0.0f,
                1.0f
            )
            renderBeamEnds(
                matrices,
                HotMRenderMaterials.getAuraNodeBeamConsumer(consumers, HotMSprites.AURA_NODE_BEAM_END, true),
                0.125f,
                len + 2f / 32f,
                outerRadius,
                0.0f,
                1.0f,
                0.0f,
                1.0f
            )
            matrices.pop()
        }

        matrices.push()
        matrices.translate(0.0, 0.35, 0.0)
        renderCrown(world, state, pos, matrices, consumers, 4, crownRadius.toDouble(), crownRoll, 0.75f)
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

    fun renderBeamEnds(
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
        renderBeamEnd(modelMat, normalMat, vertices, alpha, height, radius, u1, u2, v1, v2, true)
        renderBeamEnd(modelMat, normalMat, vertices, alpha, 0f, radius, u1, u2, v1, v2, false)
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

    private fun renderBeamEnd(
        modelMatrix: Matrix4f,
        normalMatrix: Matrix3f,
        vertices: VertexConsumer,
        alpha: Float,
        y: Float,
        radius: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float,
        top: Boolean
    ) {
        val zRadius = if (top) radius else -radius
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, y, 0f, zRadius, u2, v1)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, y, radius, 0f, u1, v1)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, y, 0f, -zRadius, u1, v2)
        renderBeamVertex(modelMatrix, normalMatrix, vertices, alpha, y, -radius, 0f, u2, v2)
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
        vertices.vertex(modelMatrix, x, y, z)
        vertices.color(1f, 1f, 1f, alpha)
        vertices.uv(u, v)
        vertices.overlay(OverlayTexture.DEFAULT_UV)
        vertices.light(15728880)
        vertices.normal(normalMatrix, 0.0f, 1.0f, 0.0f)
        vertices.next()
    }

    fun renderCrown(
        world: BlockRenderView,
        blockstate: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider,
        numPieces: Int,
        crownRadius: Double,
        crownRoll: Float,
        scale: Float
    ) {
        val increment = PI.toFloat() * 2f / numPieces.toFloat()
        for (i in 0 until numPieces) {
            matrices.push()
            matrices.multiply(Quaternionf().rotationY(i.toFloat() * increment * crownRoll))
            matrices.translate(0.0, 0.0, -crownRadius)
            matrices.scale(scale, scale, scale)
            renderCrownPiece(world, blockstate, pos, matrices, consumers)
            matrices.pop()
        }
    }

    private fun renderCrownPiece(
        world: BlockRenderView,
        blockstate: BlockState,
        pos: BlockPos,
        matrices: MatrixStack,
        consumers: VertexConsumerProvider
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
            RandomGenerator.createLegacy(42),
            42L,
            OverlayTexture.DEFAULT_UV
        )
        matrices.pop()
    }
}
