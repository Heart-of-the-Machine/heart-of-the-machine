package com.github.hotm.client.blockmodel

import com.github.hotm.client.HotMClientRegistries
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import java.util.function.Function

interface UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedModelLayer> =
            HotMClientRegistries.BLOCK_MODEL_LAYER.codec.dispatch(UnbakedModelLayer::codec, Function.identity())
    }

    val codec: Codec<out UnbakedModelLayer>

    fun getModelDependencies(): Collection<Identifier>

    fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier>

    fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModelLayer
}
