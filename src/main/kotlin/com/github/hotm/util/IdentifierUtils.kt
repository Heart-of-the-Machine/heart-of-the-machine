package com.github.hotm.util

import net.minecraft.util.Identifier

object IdentifierUtils {
    fun Identifier.extendPath(extension: String): Identifier {
        return Identifier(namespace, path + extension)
    }
}