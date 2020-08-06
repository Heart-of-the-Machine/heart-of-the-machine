package com.github.hotm

import net.minecraft.util.Identifier

object HotMConstants {
    const val MOD_ID = "hotm"

    /**
     * Creates a mod-specific identifier for the given path.
     */
    fun identifier(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}
