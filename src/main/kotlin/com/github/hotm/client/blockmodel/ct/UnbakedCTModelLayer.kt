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
import java.util.function.Function

class UnbakedCTModelLayer(
    val none: Identifier,
    val horizontal: Identifier,
    val vertical: Identifier,
    val corner: Identifier,
    val noCorner: Identifier?,
    val material: JsonMaterial
) : UnbakedModelLayer {
    companion object {
        val CODEC: Codec<UnbakedCTModelLayer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedCTModelLayer> ->
                instance.group(
                    Identifier.CODEC.fieldOf("none").forGetter(UnbakedCTModelLayer::none),
                    Identifier.CODEC.fieldOf("horizontal").forGetter(UnbakedCTModelLayer::horizontal),
                    Identifier.CODEC.fieldOf("vertical").forGetter(UnbakedCTModelLayer::vertical),
                    Identifier.CODEC.fieldOf("corner").forGetter(UnbakedCTModelLayer::corner),
                    Identifier.CODEC.fieldOf("no_corner").orElse(null).forGetter(UnbakedCTModelLayer::noCorner),
                    JsonMaterial.CODEC.fieldOf("material").orElse(JsonMaterial.DEFAULT)
                        .forGetter(UnbakedCTModelLayer::material)
                ).apply(instance) { none, horizontal, vertical, corner, noCorner, material ->
                    UnbakedCTModelLayer(none, horizontal, vertical, corner, noCorner, material)
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

        return CTModelLayer(sprites, material.toRenderMaterial())
    }

    private fun spriteId(identifier: Identifier): SpriteIdentifier {
        return SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier)
    }
}