package com.github.hotm.net.sync

import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.github.hotm.HotMConstants.str
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

object ServerSync2ClientData {
    private val ID_S2C_SYNC_2_CLIENT_DATA =
        McNetworkStack.ROOT.idData(str("SYNC_2_CLIENT_DATA")).setReadWrite(ClientSync2ClientData::onReceive, ::onSend)

    var DATA = Sync2ClientData.DEFAULT
        private set

    fun register() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            ID_S2C_SYNC_2_CLIENT_DATA.send(CoreMinecraftNetUtil.getConnection(handler.player))
        }
    }

    private fun onSend(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        ctx.assertServerSide()
        DATA.writeToPacket(buf)
    }
}