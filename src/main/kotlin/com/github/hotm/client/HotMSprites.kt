package com.github.hotm.client

import com.github.hotm.icon.HotMIcons
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier

object HotMSprites {
    lateinit var AURA_NODE_BEAM: SpriteIdentifier
        private set
    lateinit var AURA_NODE_BEAM_END: SpriteIdentifier
        private set

    fun register() {
        AURA_NODE_BEAM = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, HotMIcons.AURA_NODE_BEAM)
        AURA_NODE_BEAM_END = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, HotMIcons.AURA_NODE_BEAM_END)

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register { _, registry ->
            registry.register(HotMIcons.AURA_NODE_BEAM)
            registry.register(HotMIcons.AURA_NODE_BEAM_END)
        }
    }
}