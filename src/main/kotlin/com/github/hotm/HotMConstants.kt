package com.github.hotm

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HotMConstants {
    const val MOD_ID = "hotm"

    /**
     * Data version used in DataFixers.
     */
    const val DATA_VERSION = 99

    /**
     * Creates a mod-specific string.
     */
    fun str(str: String): String {
        return "$MOD_ID:$str"
    }

    /**
     * Creates a mod-specific identifier for the given path.
     */
    fun identifier(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }

    /**
     * Creates a TranslatableText for Heart of the Machine with the given prefix and suffix.
     */
    fun text(prefix: String, suffix: String, vararg args: Any?): MutableText {
        return Text.translatable("$prefix.$MOD_ID.$suffix", *args)
    }

    /**
     * Creates a command TranslatableText for Heart of the Machine with the given path.
     */
    fun commandText(path: String, vararg args: Any?): MutableText {
        return text("command", path, *args)
    }

    /**
     * Creates a message TranslatableText for Heart of the Machine with the given path and arguments.
     */
    fun message(path: String, vararg args: Any?): MutableText {
        return text("message", path, *args)
    }
}
