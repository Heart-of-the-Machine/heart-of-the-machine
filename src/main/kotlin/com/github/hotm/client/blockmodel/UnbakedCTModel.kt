package com.github.hotm.client.blockmodel

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import java.util.function.Function

class UnbakedCTModel(val particle: Identifier, val layers: List<UnbakedCTLayer>) : UnbakedModel, HotMBlockModel {
    companion object {
        val CODEC: Codec<UnbakedCTModel> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedCTModel> ->
                instance.group(
                    Identifier.CODEC.fieldOf("particle").forGetter(UnbakedCTModel::particle),
                    Codec.list(UnbakedCTLayer.CODEC).fieldOf("layers").forGetter(UnbakedCTModel::layers)
                ).apply(instance) { particle, layers -> UnbakedCTModel(particle, layers) }
            }
    }

    override val codec = CODEC

    override fun getModelDependencies(): Collection<Identifier> {
        return listOf()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier> {
        return (layers.flatMap { it.textureDependencies() } + SpriteIdentifier(
            SpriteAtlasTexture.BLOCK_ATLAS_TEX,
            particle
        )).toSet()
    }

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? {
        if (layers.isEmpty()) {
            throw IllegalStateException("Attempted to bake model $modelId without any layers")
        }

        return CTModel(
            modelId,
            textureGetter.apply(SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, particle)),
            layers.map { it.bake(textureGetter) }.toTypedArray(),
            layers[0].doCorners
        )
    }
}