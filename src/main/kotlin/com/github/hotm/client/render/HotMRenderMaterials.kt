package com.github.hotm.client.render

import com.github.hotm.misc.HotMLog
import grondag.frex.Frex
import grondag.frex.api.Renderer
import grondag.frex.api.material.FrexVertexConsumerProvider
import grondag.frex.api.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import java.util.function.Function

object HotMRenderMaterials {
    private lateinit var AURA_NODE_BEAM: (Identifier, Boolean) -> RenderLayer
    private lateinit var AURA_NODE_BEAM_SOLID_FREX: Function<Identifier, RenderMaterial>
//    private lateinit var AURA_NODE_BEAM_TRANSLUCENT_FREX: Function<Identifier, RenderMaterial>

    fun register() {
        if (Frex.isAvailable()) {
            HotMLog.log.info("Frex-supporting renderer detected, using fancy render materials...")

            val renderer = Renderer.get()
            val finder = renderer.materialFinder()
            finder.clear()

            AURA_NODE_BEAM_SOLID_FREX = Util.memoize { texture ->
                finder.blendMode(BlendMode.SOLID).disableAo(true).emissive(true).texture(texture).castShadows(false)
                    .find()
            }
            // Doesn't render properly. However, the bloom does what this layer was trying to do.
//            AURA_NODE_BEAM_TRANSLUCENT_FREX = Util.memoize { texture ->
//                finder.blendMode(BlendMode.TRANSLUCENT).disableAo(true).emissive(true).texture(texture).find()
//            }
        } else {
            HotMLog.log.info("No Frex-supporting renderer detected, using boring render layers...")
            AURA_NODE_BEAM = RenderLayer::getBeaconBeam
        }
    }

    fun shouldRenderOuterAuraNodeBeam(): Boolean = !Frex.isAvailable()

    fun getAuraNodeBeamConsumer(
        consumers: VertexConsumerProvider,
        texture: Identifier,
        translucent: Boolean
    ): VertexConsumer {
        return if (Frex.isAvailable() && consumers is FrexVertexConsumerProvider) {
//            if (translucent) {
//                consumers.getConsumer(AURA_NODE_BEAM_TRANSLUCENT_FREX.apply(texture))
//            } else {
            consumers.getConsumer(AURA_NODE_BEAM_SOLID_FREX.apply(texture))
//            }
        } else {
            consumers.getBuffer(AURA_NODE_BEAM(texture, translucent))
        }
    }
}