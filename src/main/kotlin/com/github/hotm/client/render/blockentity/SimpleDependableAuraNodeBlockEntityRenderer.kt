package com.github.hotm.client.render.blockentity

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.RenderedDependableAuraNode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

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

            AuraNodeRendererUtils.renderBeam(
                world,
                world.getBlockState(pos),
                pos,
                matrices,
                vertexConsumers,
                offset.x.toFloat(),
                offset.y.toFloat(),
                offset.z.toFloat(),
                world.time,
                tickDelta,
                overlay,
                0.0625f,
                0.125f
            )

            matrices.pop()
        }
    }
}