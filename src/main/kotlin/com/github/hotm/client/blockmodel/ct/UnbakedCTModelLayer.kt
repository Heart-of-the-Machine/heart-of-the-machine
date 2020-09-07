package com.github.hotm.client.blockmodel.ct

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.JsonMaterial
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
import java.util.*
import java.util.function.Function

class UnbakedCTModelLayer(
    private val none: Identifier,
    private val horizontal: Identifier,
    private val vertical: Identifier,
    private val corner: Identifier,
    private val noCorner: Identifier?,
    private val material: JsonMaterial,
    private val depth: Float,
    private val cullFaces: Boolean,
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedCTModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedCTModelLayer> ->
                instance.group(
                    Identifier.CODEC.fieldOf("none").forGetter(UnbakedCTModelLayer::none),
                    Identifier.CODEC.fieldOf("horizontal").forGetter(UnbakedCTModelLayer::horizontal),
                    Identifier.CODEC.fieldOf("vertical").forGetter(UnbakedCTModelLayer::vertical),
                    Identifier.CODEC.fieldOf("corner").forGetter(UnbakedCTModelLayer::corner),
                    Identifier.CODEC.optionalFieldOf("no_corner").forGetter { Optional.ofNullable(it.noCorner) },
                    JsonMaterial.CODEC.optionalFieldOf("material").forGetter { Optional.of(it.material) },
                    Codec.FLOAT.optionalFieldOf("depth").forGetter { Optional.of(it.depth) },
                    Codec.BOOL.optionalFieldOf("cull_faces").forGetter { Optional.of(it.cullFaces) }
                ).apply(instance) { none, horizontal, vertical, corner, noCorner, material, depth, cullFaces ->
                    UnbakedCTModelLayer(
                        none,
                        horizontal,
                        vertical,
                        corner,
                        noCorner.orElse(null),
                        material.orElse(JsonMaterial.DEFAULT),
                        depth.orElse(0.0f),
                        cullFaces.orElse(true)
                    )
                }
            }
    }

    override val codec = CODEC

    private val doCorners = noCorner != null

    override fun getModelDependencies(): Collection<Identifier> {
        return emptyList()
    }

    override fun getTextureDependencies(
        unbakedModelGetter: Function<Identifier, UnbakedModel>?,
        unresolvedTextureReferences: MutableSet<Pair<String, String>>?
    ): Collection<SpriteIdentifier> {
        return if (doCorners) {
            listOf(spriteId(none), spriteId(horizontal), spriteId(vertical), spriteId(corner), spriteId(noCorner!!))
        } else {
            listOf(spriteId(none), spriteId(horizontal), spriteId(vertical), spriteId(corner))
        }
    }

    override fun bake(
        loader: ModelLoader,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModelLayer {
        fun sprite(identifier: Identifier): Sprite {
            return textureGetter.apply(spriteId(identifier))
        }

        val sprites = if (doCorners) {
            arrayOf(sprite(none), sprite(horizontal), sprite(vertical), sprite(corner), sprite(noCorner!!))
        } else {
            arrayOf(sprite(none), sprite(horizontal), sprite(vertical), sprite(corner))
        }

        return CTModelLayer(sprites, material.toRenderMaterial(), depth, cullFaces)
    }

    private fun spriteId(identifier: Identifier): SpriteIdentifier {
        return SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier)
    }
}