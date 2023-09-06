package com.github.hotm.mod.client.rendering.aura

import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.node.aura.AuraLinkEntity
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.RenderLayer
import com.kneelawk.graphlib.api.client.render.RenderUtils

object HotMAuraLinkRendering {
    fun init() {
        WorldRenderEvents.AFTER_ENTITIES.register(::draw)
    }

    private fun draw(ctx: WorldRenderContext) {
        val graphWorld = HotMUniverses.NETWORKS.clientGraphView ?: return
        val consumers = ctx.consumers() ?: return
        val stack = ctx.matrixStack()
        val cameraPos = ctx.camera().pos

        stack.push()
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        for (graph in graphWorld.allGraphs) {
            for (link in graph.linkEntities) {
                val auraLink = link as? AuraLinkEntity ?: continue
                val parent = auraLink.parent
                val pos1 = parent.pos
                val child = auraLink.context.holder.other(parent).pos
                val pos2 = child.pos

                RenderUtils.drawLine(
                    stack,
                    consumers.getBuffer(RenderLayer.LINES),
                    pos1.x + 0.5f,
                    pos1.y + 0.5f,
                    pos1.z + 0.5f,
                    pos2.x + 0.5f,
                    pos2.y + 0.5f,
                    pos2.z + 0.5f,
                    0xFFFFFFFF.toInt()
                )
            }
        }

        stack.pop()
    }
}
