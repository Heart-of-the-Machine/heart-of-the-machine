package com.github.hotm.util

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec

object CodecUtils {
    val PREFER_FLOAT_OR_INT: Codec<Float> = Codec.either(Codec.FLOAT, Codec.INT)
        .xmap({ either -> either.map({ it }, { it.toFloat() }) }, { f -> Either.left(f) })
}