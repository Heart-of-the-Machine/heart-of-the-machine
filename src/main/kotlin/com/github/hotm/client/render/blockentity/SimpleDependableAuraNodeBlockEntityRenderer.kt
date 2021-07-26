package com.github.hotm.client.render.blockentity

import com.github.hotm.blockentity.RenderedDependableAuraNodeBlockEntity
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.RenderedDependableAuraNode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

open class SimpleDependableAuraNodeBlockEntityRenderer<T>(protected val ctx: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<T> where T : BlockEntity, T : RenderedDependableAuraNodeBlockEntity {
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

        // This is null when a node is placed but before the client has received the node data
        val node = access[pos] as? RenderedDependableAuraNode ?: return
        val children = node.getChildrenForRender()

        entity.updateRenderValues(world.time, tickDelta)

        for (childPos in children) {
            val offset = childPos.subtract(pos)

            if (offset == pos) {
                return
            }

            val energy = node.getSuppliedAuraForRender(childPos)
            val rollSpeed = node.getCrownRollSpeed(childPos)
            val roll = entity.getAndUpdateCrownRoll(childPos, rollSpeed)

            matrices.push()

            matrices.translate(0.5, 0.5, 0.5)

            AuraNodeRendererUtils.renderBeam(
                world,
                pos,
                matrices,
                vertexConsumers,
                tickDelta,
                overlay,
                offset,
                energy,
                roll
            )

            matrices.pop()
        }
    }
}