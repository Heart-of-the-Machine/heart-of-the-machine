package com.github.hotm.mod.client

import com.github.hotm.mod.Log
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

@Suppress("unused")
object HotMModClient : ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer) {
        Log.LOG.info("[HotM] Initializing Heart of the Machine Client...")

        HotMBlocksClient.init()
        HotMColorProviders.init()

        Log.LOG.info("[HotM] Heart of the Machine Client initialized.")
    }
}
