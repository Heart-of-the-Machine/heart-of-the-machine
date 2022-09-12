package com.github.hotm.client.render

import com.mojang.blaze3d.vertex.VertexConsumer
import io.vram.frex.api.buffer.VertexEmitter
import io.vram.frex.api.config.FrexFeature
import io.vram.frex.api.material.MaterialConstants
import io.vram.frex.api.material.MaterialFinder
import io.vram.frex.api.material.RenderMaterial
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.SpriteTexturedVertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier

object HotMRenderMaterials {
    private lateinit var AURA_NODE_BEAM: (Identifier, Boolean) -> RenderLayer
    private lateinit var AURA_NODE_BEAM_SOLID_FREX: RenderMaterial
    private lateinit var AURA_NODE_BEAM_TRANSLUCENT_FREX: RenderMaterial

    fun register() {
        val finder = MaterialFinder.threadLocal()
        finder.clear()

        AURA_NODE_BEAM_SOLID_FREX =
            finder.preset(MaterialConstants.PRESET_SOLID).disableAo(true).disableDiffuse(true).emissive(true)
                .castShadows(false).find()
        // TODO: check if this actually renders properly on Pastel
        AURA_NODE_BEAM_TRANSLUCENT_FREX =
            finder.preset(MaterialConstants.PRESET_TRANSLUCENT).disableAo(true).disableDiffuse(true).emissive(true)
                .castShadows(false).find()

        AURA_NODE_BEAM = RenderLayer::getBeaconBeam
    }

    // anytning that supports material shaders will probably also render the aura node beam material correctly
    fun shouldRenderOuterAuraNodeBeam(): Boolean = !FrexFeature.isAvailable(FrexFeature.MATERIAL_SHADERS)

    fun getAuraNodeBeamConsumer(
        consumers: VertexConsumerProvider,
        texture: SpriteIdentifier,
        translucent: Boolean
    ): VertexConsumer {
        val consumer = consumers.getBuffer(AURA_NODE_BEAM(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, translucent))

        if (consumer is VertexEmitter) {
            if (translucent) {
                consumer.material(AURA_NODE_BEAM_TRANSLUCENT_FREX)
            } else {
                consumer.material(AURA_NODE_BEAM_SOLID_FREX)
            }
        }

        return SpriteTexturedVertexConsumer(consumer, texture.sprite)
    }
}
