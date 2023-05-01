package com.github.hotm.mod

import org.quiltmc.loader.api.QuiltLoader

object Constants {
    const val MOD_ID = "hotm"

    val MOD_VERSION = QuiltLoader.getModContainer(MOD_ID).get().metadata().version().raw()
}
