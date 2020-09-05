package com.github.hotm.client.blockmodel

import com.mojang.datafixers.util.Pair
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import java.util.function.Function

class UnbakedConnectedTextureModel(
    none: Identifier,
    horizontal: Identifier,
    vertical: Identifier,
    corner: Identifier,
    noCorner: Identifier
) : UnbakedModel {
    private val sprites = listOf(
        SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, none),
        SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, horizontal),
        SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, vertical),
        SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, corner),
        SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, noCorner)
    )

    override fun getModelDependencies(): Collection<Identifier> {
        return listOf()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier> {
        return sprites
    }

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? {
        return ConnectedTextureModel(sprites.map { textureGetter.apply(it) })
    }
}