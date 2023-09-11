package com.github.hotm.mod.client.rendering

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.util.Identifier

object RenderUtils {
    fun getBlockSprite(id: Identifier): Sprite {
        return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(id)
    }

    fun getModel(id: Identifier): BakedModel {
        val manager = MinecraftClient.getInstance().bakedModelManager
        return manager.getModel(id) ?: manager.missingModel
    }
}
