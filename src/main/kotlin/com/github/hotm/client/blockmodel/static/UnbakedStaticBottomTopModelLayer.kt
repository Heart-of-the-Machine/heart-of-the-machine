package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.JsonMaterial
import com.github.hotm.client.blockmodel.UnbakedModelLayer
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import java.util.function.Function

class UnbakedStaticBottomTopModelLayer(
    private val side: Identifier,
    private val bottom: Identifier,
    private val top: Identifier,
    private val material: JsonMaterial,
    private val depth: Float,
    private val cullFaces: Boolean,
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedStaticBottomTopModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedStaticBottomTopModelLayer> ->
                instance.group(
                    Identifier.CODEC.fieldOf("side").forGetter(UnbakedStaticBottomTopModelLayer::side),
                    Identifier.CODEC.fieldOf("bottom").forGetter(UnbakedStaticBottomTopModelLayer::bottom),
                    Identifier.CODEC.fieldOf("top").forGetter(UnbakedStaticBottomTopModelLayer::top),
                    JsonMaterial.CODEC.fieldOf("material").orElse(JsonMaterial.DEFAULT)
                        .forGetter(UnbakedStaticBottomTopModelLayer::material),
                    Codec.FLOAT.fieldOf("depth").orElse(0.0f).forGetter(UnbakedStaticBottomTopModelLayer::depth),
                    Codec.BOOL.fieldOf("cull_faces").orElse(true).forGetter(UnbakedStaticBottomTopModelLayer::cullFaces)
                ).apply(instance) { side, bottom, top, material, depth, cullFaces ->
                    UnbakedStaticBottomTopModelLayer(side, bottom, top, material, depth, cullFaces)
                }
            }

        private val EXTRA_FLAGS_PER_AXIS = arrayOf(
            0,
            MutableQuadView.BAKE_FLIP_V,
            0,
        )
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
        val renderer = RendererAccess.INSTANCE.renderer
        val renderMaterial = material.toRenderMaterial()
        val meshBuilder = renderer.meshBuilder()
        val emitter = meshBuilder.emitter
        val sideSprite = textureGetter.apply(sideSpriteId)
        val bottomSprite = textureGetter.apply(bottomSpriteId)
        val topSprite = textureGetter.apply(topSpriteId)

        for (normal in Direction.values()) {
            emitter.square(
                normal,
                0.0f + depthClamped,
                0.0f + depthClamped,
                1.0f - depthClamped,
                1.0f - depthClamped,
                depthMaxed
            )
            emitter.spriteColor(0, -1, -1, -1, -1)
            emitter.material(renderMaterial)

            emitter.spriteBake(
                0, when (normal) {
                    Direction.DOWN -> bottomSprite
                    Direction.UP -> topSprite
                    else -> sideSprite
                }, MutableQuadView.BAKE_LOCK_UV or EXTRA_FLAGS_PER_AXIS[normal.axis.ordinal]
            )

            emitter.cullFace(
                if (cullFaces) {
                    normal
                } else {
                    null
                }
            )

            emitter.emit()
        }

        return StaticModelLayer(meshBuilder.build())
    }
}