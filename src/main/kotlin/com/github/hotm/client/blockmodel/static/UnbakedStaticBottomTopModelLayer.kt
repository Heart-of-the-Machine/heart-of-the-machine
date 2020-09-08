package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.JsonMaterial
import com.github.hotm.client.blockmodel.UnbakedModelLayer
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
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
    private val side: Identifier,
    private val bottom: Identifier,
    private val top: Identifier,
    private val material: JsonMaterial,
    private val depth: Float,
    private val cullFaces: Boolean,
    private val rotate: Boolean,
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedStaticBottomTopModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedStaticBottomTopModelLayer> ->
                instance.group(
                    Identifier.CODEC.fieldOf("side").forGetter(UnbakedStaticBottomTopModelLayer::side),
                    Identifier.CODEC.fieldOf("bottom").forGetter(UnbakedStaticBottomTopModelLayer::bottom),
                    Identifier.CODEC.fieldOf("top").forGetter(UnbakedStaticBottomTopModelLayer::top),
                    JsonMaterial.CODEC.optionalFieldOf("material").forGetter { Optional.of(it.material) },
                    Codec.FLOAT.optionalFieldOf("depth").forGetter { Optional.of(it.depth) },
                    Codec.BOOL.optionalFieldOf("cull_faces").forGetter { Optional.of(it.cullFaces) },
                    Codec.BOOL.optionalFieldOf("rotate").forGetter { Optional.of(it.rotate) }
                ).apply(instance) { side, bottom, top, material, depth, cullFaces, rotate ->
                    UnbakedStaticBottomTopModelLayer(
                        side,
                        bottom,
                        top,
                        material.orElse(JsonMaterial.DEFAULT),
                        depth.orElse(0.0f),
                        cullFaces.orElse(true),
                        rotate.orElse(true)
                    )
                }
            }
    }

    private val depthClamped = MathHelper.clamp(depth, 0.0f, 0.5f)
    private val depthMaxed = depth.coerceAtMost(0.5f)
    private val sideSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, side)
    private val bottomSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, bottom)
    private val topSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, top)

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
            bottomSprite,
            topSprite,
            sideSprite,
            sideSprite,
            sideSprite,
            sideSprite
        )
    }
}