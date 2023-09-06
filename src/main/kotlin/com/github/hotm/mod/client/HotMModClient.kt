package com.github.hotm.mod.client

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.client.rendering.aura.HotMAuraLinkRendering
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

@Suppress("unused")
object HotMModClient : ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer) {
        HotMLog.LOG.info("[HotM] Initializing Heart of the Machine Client...")

        HotMBlocksClient.init()
        HotMColorProviders.init()
        HotMAuraLinkRendering.init()

        HotMLog.LOG.info("[HotM] Heart of the Machine Client initialized.")
    }
}
