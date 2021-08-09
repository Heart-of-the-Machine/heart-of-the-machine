package com.github.hotm.net.sync

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object ClientSync2ClientData {
    var DATA = Sync2ClientData.DEFAULT
        private set

    fun register() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            DATA = Sync2ClientData.DEFAULT
        }
    }

    internal fun onReceive(buf: NetByteBuf, ctx: IMsgReadCtx) {
        ctx.assertClientSide()
        DATA = Sync2ClientData.readFromPacket(buf)
    }
}