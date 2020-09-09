package com.github.hotm.client.blockmodel

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.Function.identity

data class JsonTexture(val texture: Identifier, val tintIndex: Int) {
    companion object {
        private val RECORD_CODEC: Codec<JsonTexture> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<JsonTexture> ->
                instance.group(
                    Identifier.CODEC.fieldOf("texture").forGetter(JsonTexture::texture),
                    Codec.INT.optionalFieldOf("tintindex").forGetter { Optional.of(it.tintIndex) }
                ).apply(instance) { texture, tintIndex ->
                    JsonTexture(texture, tintIndex.orElse(-1))
                }
            }
        val CODEC: Codec<JsonTexture> = Codec.either(Identifier.CODEC, RECORD_CODEC)
            .xmap({ it.map({ id -> JsonTexture(id, -1) }, identity()) }, { Either.right(it) })
    }
}