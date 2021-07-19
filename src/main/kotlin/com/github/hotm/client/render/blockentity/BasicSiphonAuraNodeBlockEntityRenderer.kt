package com.github.hotm.client.render.blockentity

import com.github.hotm.blockentity.BasicSiphonAuraNodeBlockEntity
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.BasicSiphonAuraNode
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3f

class BasicSiphonAuraNodeBlockEntityRenderer(ctx: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<BasicSiphonAuraNodeBlockEntity> {
    override fun rendersOutsideBoundingBox(blockEntity: BasicSiphonAuraNodeBlockEntity?): Boolean {
        return true
    }

    override fun render(
        entity: BasicSiphonAuraNodeBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val world = entity.world ?: return
        val pos = entity.pos
        val access = StorageUtils.getAuraNetAccess(world)
        val node = access[pos] as? BasicSiphonAuraNode ?: return
        val childPos = node.childPos ?: return

        val offset = childPos.subtract(pos)

        val normal = Vec3f(offset.x.toFloat(), offset.y.toFloat(), offset.z.toFloat())
        if (!normal.normalize()) {
            // the two nodes are at the same place
            return
        }

        matrices.push()

        val consumer = vertexConsumers.getBuffer(RenderLayer.getLines())

        val modelMat = matrices.peek().model
        val normalMat = matrices.peek().normal
        consumer.vertex(modelMat, 0.5f, 0.5f, 0.5f).color(1f, 1f, 1f, 1f)
            .normal(normalMat, normal.x, normal.y, normal.z).next()
        consumer.vertex(modelMat, offset.x + 0.5f, offset.y + 0.5f, offset.z + 0.5f).color(1f, 1f, 1f, 1f)
            .normal(normalMat, -normal.x, -normal.y, -normal.z).next()

        matrices.pop()
    }
}