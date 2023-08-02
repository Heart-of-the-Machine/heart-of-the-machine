package com.github.hotm.mod.util

import com.github.hotm.mod.HotMLog
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer

object CurrentServer {
    private var server: MinecraftServer? = null

    fun get(): MinecraftServer = server ?: throw IllegalStateException("No Minecraft server present")

    fun tryGet(): MinecraftServer? = server

    fun init() {
        ServerLifecycleEvents.STARTING.register {
            if (server != null) {
                HotMLog.LOG.error("Attempted to set the current server while the server is already running!")
                return@register
            }
            HotMLog.LOG.info("Grabbing minecraft server instance")
            server = it
        }
        ServerLifecycleEvents.STOPPED.register {
            HotMLog.LOG.info("Releasing minecraft server instance")
            server = null
        }
    }
}
