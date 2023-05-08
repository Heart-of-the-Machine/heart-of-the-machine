package com.github.hotm.mod

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.misc.HotMCreativeTabs
import com.github.hotm.mod.world.gen.carver.HotMCarvers
import com.github.hotm.mod.world.gen.surfacebuilder.HotMSurfaceBuilders
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("unused")
object HotMMod : ModInitializer {
    override fun onInitialize(mod: ModContainer) {
        Log.LOG.info("[HotM] Initializing Heart of the Machine v${Constants.MOD_VERSION}...")

        HotMBlocks.init()
        HotMCarvers.init()
        HotMSurfaceBuilders.init()

        HotMCreativeTabs.init()

        Log.LOG.info("[HotM] Heart of the Machine initialized.")
    }
}
