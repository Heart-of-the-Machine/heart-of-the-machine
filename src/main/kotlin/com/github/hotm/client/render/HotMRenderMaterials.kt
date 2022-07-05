package com.github.hotm.client.render

import io.vram.frex.api.config.FrexFeature
import io.vram.frex.api.material.MaterialConstants
import io.vram.frex.api.material.MaterialFinder
import io.vram.frex.api.material.RenderMaterial
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.SpriteTexturedVertexConsumer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier

object HotMRenderMaterials {
    private lateinit var AURA_NODE_BEAM: (Identifier, Boolean) -> RenderLayer
    private lateinit var AURA_NODE_BEAM_SOLID_FREX: RenderMaterial
//    private lateinit var AURA_NODE_BEAM_TRANSLUCENT_FREX: Function<Identifier, RenderMaterial>

    fun register() {
        val finder = MaterialFinder.threadLocal()
        finder.clear()

        AURA_NODE_BEAM_SOLID_FREX =
            finder.preset(MaterialConstants.PRESET_SOLID).disableAo(true).disableDiffuse(true).emissive(true)
                .castShadows(false).find()
        // Doesn't render properly. However, the bloom does what this layer was trying to do.
//            AURA_NODE_BEAM_TRANSLUCENT_FREX = Util.memoize { texture ->
//                finder.blendMode(BlendMode.TRANSLUCENT).disableAo(true).emissive(true).texture(texture).find()
//            }

        AURA_NODE_BEAM = RenderLayer::getBeaconBeam
    }

    // anytning that supports material shaders will probably also render the aura node beam material correctly
    fun shouldRenderOuterAuraNodeBeam(): Boolean = !FrexFeature.isAvailable(FrexFeature.MATERIAL_SHADERS)

    fun getAuraNodeBeamConsumer(
        consumers: VertexConsumerProvider,
        texture: SpriteIdentifier,
        translucent: Boolean
    ): VertexConsumer {
        return SpriteTexturedVertexConsumer(
            if (consumers is FrexVertexConsumerProvider) {
//            if (translucent) {
//                consumers.getConsumer(AURA_NODE_BEAM_TRANSLUCENT_FREX.apply(texture))
//            } else {
                consumers.getConsumer(AURA_NODE_BEAM_SOLID_FREX)
//            }
            } else {
                consumers.getBuffer(AURA_NODE_BEAM(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, translucent))
            }, texture.sprite
        )
    }
}
