package com.github.hotm.mod

import org.quiltmc.loader.api.QuiltLoader
import net.minecraft.util.Identifier

object Constants {
    const val MOD_ID = "hotm"

    val MOD_VERSION: String = QuiltLoader.getModContainer(MOD_ID).get().metadata().version().raw()

    fun id(path: String): Identifier = Identifier(MOD_ID, path)
}
