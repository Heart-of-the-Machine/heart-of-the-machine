package com.github.hotm

import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

object HotMConstants {
    const val MOD_ID = "hotm"

    /**
     * Creates a mod-specific identifier for the given path.
     */
    fun identifier(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }

    /**
     * Creates a TranslatableText for Heart of the Machine with the given prefix and suffix.
     */
    fun text(prefix: String, suffix: String, vararg args: Any): TranslatableText {
        return TranslatableText("$prefix.$MOD_ID.$suffix", *args)
    }

    /**
     * Creates a command TranslatableText for Heart of the Machine with the given path.
     */
    fun commandText(path: String, vararg args: Any): TranslatableText {
        return text("command", path, *args)
    }
}
