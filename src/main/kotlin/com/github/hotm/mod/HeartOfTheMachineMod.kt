package com.github.hotm.mod

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

object HeartOfTheMachineMod : ModInitializer {
    override fun onInitialize(mod: ModContainer?) {
        Log.LOG.info("[HotM] Initializing Heart of the Machine v${Constants.MOD_VERSION}...")

        Log.LOG.info("[HotM] Heart of the Machine initialized.")
    }
}
