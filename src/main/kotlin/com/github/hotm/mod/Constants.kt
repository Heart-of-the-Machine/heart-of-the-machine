package com.github.hotm.mod

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.QuiltLoader

object Constants {
    const val MOD_ID = "hotm"

    val MOD_VERSION: String = QuiltLoader.getModContainer(MOD_ID).get().metadata().version().raw()

    fun id(path: String): Identifier = Identifier(MOD_ID, path)

    fun str(path: String): String = "$MOD_ID:$path"

    fun tt(prefix: String, suffix: String, vararg args: Any?): MutableText =
        Text.translatable("$prefix.$MOD_ID.$suffix", *args)

    fun msg(suffix: String, vararg args: Any?): MutableText = tt("message", suffix, *args)
}
