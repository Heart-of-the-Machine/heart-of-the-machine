package com.github.hotm.mod

import com.github.hotm.mod.world.gen.carver.HotMCarvers
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

object HotMMod : ModInitializer {
    override fun onInitialize(mod: ModContainer) {
        Log.LOG.info("[HotM] Initializing Heart of the Machine v${Constants.MOD_VERSION}...")

        HotMCarvers.init()

        Log.LOG.info("[HotM] Heart of the Machine initialized.")
    }
}
