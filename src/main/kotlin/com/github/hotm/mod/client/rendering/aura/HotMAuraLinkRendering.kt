package com.github.hotm.mod.client.rendering.aura

import com.github.hotm.mod.auranet.AuraNode
import com.github.hotm.mod.mixin.api.HotMClientMixinHelper
import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.node.aura.AuraLinkEntity
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.util.math.Vec3d
import com.kneelawk.graphlib.api.graph.user.NodeEntityType

object HotMAuraLinkRendering {
    private val nodeRenderers = mutableMapOf<NodeEntityType, AuraNodeRenderer<*>>()

    private var lastWorldTime = 0L
    private var lastTickDelta = 0f

    fun registerAuraNodeRenderer(type: NodeEntityType, renderer: AuraNodeRenderer<*>) {
        nodeRenderers[type] = renderer
    }

    fun init() {
        WorldRenderEvents.AFTER_ENTITIES.register(::draw)
    }

    private fun draw(ctx: WorldRenderContext) {
        val graphWorld = HotMUniverses.NETWORKS.clientGraphView ?: return
        val consumers = ctx.consumers() ?: return
        val frustum = ctx.frustum() ?: return
        val stack = ctx.matrixStack()
        val cameraPos = ctx.camera().pos

        var dwt = ctx.world().time - lastWorldTime
        val dtd = ctx.tickDelta() - lastTickDelta
        lastWorldTime = ctx.world().time
        lastTickDelta = ctx.tickDelta()

        if (dwt > 50L) dwt = 0

        val delta = dwt.toFloat() + dtd

        stack.push()
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        for (graph in graphWorld.allGraphs) {
            for (entity in graph.linkEntities) {
                val linkEntity = entity as? AuraLinkEntity ?: continue
                val link = linkEntity.context.holder
                val parentPos = linkEntity.parent
                val childHolder = link.other(parentPos)
                val parentHolder = link.other(childHolder)

                val parent = parentHolder.getNodeEntity(AuraNode::class.java) ?: continue
                val child = childHolder.getNodeEntity(AuraNode::class.java) ?: continue

                val parentOffset = (nodeRenderers[parent.type] as? AuraNodeRenderer<AuraNode>)?.getLinkPosOffset(parent)
                    ?: Vec3d(0.5, 0.5, 0.5)
                val childOffset = (nodeRenderers[child.type] as? AuraNodeRenderer<AuraNode>)?.getLinkPosOffset(child)
                    ?: Vec3d(0.5, 0.5, 0.5)

                val parentVec = Vec3d.of(parentHolder.blockPos).add(parentOffset)
                val childVec = Vec3d.of(childHolder.blockPos).add(childOffset)

                if (!HotMClientMixinHelper.isLineSegmentVisible(frustum, parentVec, childVec)) continue

                stack.push()
                stack.translate(parentVec.x, parentVec.y, parentVec.z)

                val crownRoll = linkEntity.incrementAndGetCrownRoll(linkEntity.value * Math.PI.toFloat() / 60f * delta)

                AuraNodeRendererUtils.renderBeam(
                    ctx.world(),
                    parentHolder.blockPos,
                    stack,
                    consumers,
                    ctx.tickDelta(),
                    childVec.subtract(parentVec),
                    linkEntity.value,
                    crownRoll
                )

                stack.pop()

//                RenderUtils.drawLine(
//                    stack,
//                    consumers.getBuffer(RenderLayer.LINES),
//                    parentVec.x.toFloat(),
//                    parentVec.y.toFloat(),
//                    parentVec.z.toFloat(),
//                    childVec.x.toFloat(),
//                    childVec.y.toFloat(),
//                    childVec.z.toFloat(),
//                    0xFFFFFFFF.toInt()
//                )
            }
        }

        stack.pop()
    }
}
