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

class UnbakedStaticModelLayer(
    private val all: Identifier,
    private val material: JsonMaterial,
    private val depth: Float,
    private val cullFaces: Boolean
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedStaticModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedStaticModelLayer> ->
                instance.group(
                    Identifier.CODEC.fieldOf("all").forGetter(UnbakedStaticModelLayer::all),
                    JsonMaterial.CODEC.fieldOf("material").orElse(JsonMaterial.DEFAULT)
                        .forGetter(UnbakedStaticModelLayer::material),
                    Codec.FLOAT.fieldOf("depth").orElse(0.0f).forGetter(UnbakedStaticModelLayer::depth),
                    Codec.BOOL.fieldOf("cull_faces").orElse(true).forGetter(UnbakedStaticModelLayer::cullFaces)
                ).apply(instance) { all, material, depth, cullFaces ->
                    UnbakedStaticModelLayer(all, material, depth, cullFaces)
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

    override val codec = CODEC

    private val allSpriteId = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, all)

    override fun getModelDependencies(): Collection<Identifier> {
        return emptyList()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier> {
        return listOf(allSpriteId)
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
        val allSprite = textureGetter.apply(allSpriteId)

        for (face in Direction.values()) {
            emitter.square(
                face,
                0.0f + depthClamped,
                0.0f + depthClamped,
                1.0f - depthClamped,
                1.0f - depthClamped,
                depthMaxed
            )
            emitter.spriteBake(0, allSprite, MutableQuadView.BAKE_LOCK_UV or EXTRA_FLAGS_PER_AXIS[face.axis.ordinal])
            emitter.spriteColor(0, -1, -1, -1, -1)
            emitter.material(renderMaterial)

            emitter.cullFace(
                if (cullFaces) {
                    face
                } else {
                    null
                }
            )

            emitter.emit()
        }

        return StaticModelLayer(meshBuilder.build())
    }
}