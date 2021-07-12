package com.github.hotm.util

import java.util.stream.Stream

object StreamUtils {
    fun <T> ofNullable(obj: T?): Stream<T> {
        return if (obj == null) {
            Stream.empty()
        } else {
            Stream.of(obj)
        }
    }
}