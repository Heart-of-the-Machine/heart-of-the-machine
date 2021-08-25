package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.JsonMaterial
import com.github.hotm.client.blockmodel.JsonTexture
import com.github.hotm.client.blockmodel.UnbakedModelLayer
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.util.*
import java.util.function.Function

class UnbakedStaticBottomTopModelLayer(
    private val side: JsonTexture,
    private val bottom: JsonTexture,
    private val top: JsonTexture,
    private val material: JsonMaterial,
    private val depth: Float,
    private val cullFaces: Boolean,
    private val rotate: Boolean,
    private val quarterFaces: Boolean,
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedStaticBottomTopModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedStaticBottomTopModelLayer> ->
                instance.group(
                    JsonTexture.CODEC.fieldOf("side").forGetter(UnbakedStaticBottomTopModelLayer::side),
                    JsonTexture.CODEC.fieldOf("bottom").forGetter(UnbakedStaticBottomTopModelLayer::bottom),
                    JsonTexture.CODEC.fieldOf("top").forGetter(UnbakedStaticBottomTopModelLayer::top),
                    JsonMaterial.CODEC.optionalFieldOf("material").forGetter { Optional.of(it.material) },
                    Codec.FLOAT.optionalFieldOf("depth").forGetter { Optional.of(it.depth) },
                    Codec.BOOL.optionalFieldOf("cull_faces").forGetter { Optional.of(it.cullFaces) },
                    Codec.BOOL.optionalFieldOf("rotate").forGetter { Optional.of(it.rotate) },
                    Codec.BOOL.optionalFieldOf("quarter_faces").forGetter { Optional.of(it.quarterFaces) }
                ).apply(instance) { side, bottom, top, material, depth, cullFaces, rotate, quarterFaces ->
                    UnbakedStaticBottomTopModelLayer(
                        side,
                        bottom,
                        top,
                        material.orElse(JsonMaterial.DEFAULT),
                        depth.orElse(0.0f),
                        cullFaces.orElse(true),
                        rotate.orElse(true),
                        quarterFaces.orElse(false)
                    )
                }
            }
    }

    private val depthClamped = MathHelper.clamp(depth, 0.0f, 0.5f)
    private val depthMaxed = depth.coerceAtMost(0.5f)
    private val sideSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, side.texture)
    private val bottomSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, bottom.texture)
    private val topSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, top.texture)

    override val codec = CODEC

    override fun getModelDependencies(): Collection<Identifier> {
        return emptyList()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier> {
        return listOf(sideSpriteId, bottomSpriteId, topSpriteId)
    }

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModelLayer {
        val sideSprite = textureGetter.apply(sideSpriteId)
        val bottomSprite = textureGetter.apply(bottomSpriteId)
        val topSprite = textureGetter.apply(topSpriteId)

        return StaticModelLayer.createBlock(
            rotationContainer,
            material.toRenderMaterial(),
            rotate,
            cullFaces,
            depthClamped,
            depthMaxed,
            quarterFaces,
            bottomSprite,
            bottom.tintIndex,
            topSprite,
            top.tintIndex,
            sideSprite,
            side.tintIndex,
            sideSprite,
            side.tintIndex,
            sideSprite,
            side.tintIndex,
            sideSprite,
            side.tintIndex
        )
    }
}