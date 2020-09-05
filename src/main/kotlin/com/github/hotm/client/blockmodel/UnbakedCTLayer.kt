package com.github.hotm.client.blockmodel

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import java.util.function.Function

data class UnbakedCTLayer(
    val none: Identifier,
    val horizontal: Identifier,
    val vertical: Identifier,
    val corner: Identifier,
    val noCorner: Identifier?,
    val material: JsonMaterial
) {
    companion object {
        val CODEC: Codec<UnbakedCTLayer> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<UnbakedCTLayer> ->
            instance.group(
                Identifier.CODEC.fieldOf("none").forGetter(UnbakedCTLayer::none),
                Identifier.CODEC.fieldOf("horizontal").forGetter(UnbakedCTLayer::horizontal),
                Identifier.CODEC.fieldOf("vertical").forGetter(UnbakedCTLayer::vertical),
                Identifier.CODEC.fieldOf("corner").forGetter(UnbakedCTLayer::corner),
                Identifier.CODEC.fieldOf("no_corner").orElse(null).forGetter(UnbakedCTLayer::noCorner),
                JsonMaterial.CODEC.fieldOf("material").orElse(JsonMaterial.DEFAULT).forGetter(UnbakedCTLayer::material)
            ).apply(instance) { none, horizontal, vertical, corner, noCorner, material ->
                UnbakedCTLayer(none, horizontal, vertical, corner, noCorner, material)
            }
        }
    }

    val doCorners: Boolean
        get() = noCorner != null

    fun textureDependencies(): List<SpriteIdentifier> {
        fun spriteId(identifier: Identifier): SpriteIdentifier {
            return SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier)
        }

        return if (doCorners) {
            listOf(spriteId(none), spriteId(horizontal), spriteId(vertical), spriteId(corner), spriteId(noCorner!!))
        } else {
            listOf(spriteId(none), spriteId(horizontal), spriteId(vertical), spriteId(corner))
        }
    }

    fun bake(textureGetter: Function<SpriteIdentifier, Sprite>): CTModel.Layer {
        fun sprite(identifier: Identifier): Sprite {
            return textureGetter.apply(SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier))
        }

        val sprites = if (doCorners) {
            arrayOf(sprite(none), sprite(horizontal), sprite(vertical), sprite(corner), sprite(noCorner!!))
        } else {
            arrayOf(sprite(none), sprite(horizontal), sprite(vertical), sprite(corner))
        }

        return CTModel.Layer(sprites, material.toRenderMaterial())
    }
}
