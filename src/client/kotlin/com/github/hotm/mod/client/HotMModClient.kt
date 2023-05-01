package com.github.hotm.mod.client

import com.github.hotm.mod.Log
import net.minecraft.client.MinecraftClient
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

object HotMModClient : ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer?) {
        Log.LOG.info("[HotM] Initializing Heart of the Machine Client...")

        Log.LOG.info("[HotM] MinecraftClient: " + MinecraftClient.getModStatus().description)

        Log.LOG.info("[HotM] Heart of the Machine Client initialized.")
    }
}
