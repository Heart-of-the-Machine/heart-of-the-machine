package com.github.hotm.client.blockmodel

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.Function

class UnbakedLayeredModel(
    private val transformation: Identifier,
    private val particle: Identifier,
    private val layers: List<UnbakedModelLayer>
) : UnbakedModel {
    companion object {
        private val DEFAULT_TRANSFORMATION = Identifier("minecraft:block/block")

        val CODEC: Codec<UnbakedLayeredModel> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedLayeredModel> ->
                instance.group(
                    Identifier.CODEC.optionalFieldOf("transformation").forGetter { Optional.of(it.transformation) },
                    Identifier.CODEC.fieldOf("particle").forGetter(UnbakedLayeredModel::particle),
                    Codec.list(UnbakedModelLayer.CODEC).fieldOf("layers").forGetter(UnbakedLayeredModel::layers)
                ).apply(instance) { transformation, particle, layers ->
                    UnbakedLayeredModel(transformation.orElse(DEFAULT_TRANSFORMATION), particle, layers)
                }
            }
    }

    override val codec = CODEC

    override fun getModelDependencies(): Collection<Identifier> {
        return (layers.flatMap { it.getModelDependencies() } + transformation).toSet()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, net.minecraft.client.render.model.UnbakedModel>,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>
    ): Collection<SpriteIdentifier> {
        return (layers.flatMap {
            it.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences)
        } + SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, particle)).toSet()
    }

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? {
        val transformationModel = loader.getOrLoadModel(transformation) as? JsonUnbakedModel
            ?: throw IllegalStateException("Unable to load transformation $transformation model as JsonUnbakedModel for model $modelId")

        if (layers.isEmpty()) {
            throw IllegalStateException("Attempted to bake model $modelId without any layers")
        }

        return LayeredModel(
            transformationModel.transformations,
            textureGetter.apply(SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, particle)),
            layers.map { it.bake(loader, textureGetter, rotationContainer, modelId) }.toTypedArray()
        )
    }
}
