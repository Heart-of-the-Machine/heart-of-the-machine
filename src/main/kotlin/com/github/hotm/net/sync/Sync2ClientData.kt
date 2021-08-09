package com.github.hotm.net.sync

import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.config.HotMConfig

data class Sync2ClientData(val nectereAuraBaseValue: Float, val nonNectereAuraBaseValue: Float) {
    companion object {
        val DEFAULT =
            Sync2ClientData(HotMConfig.CONFIG.nectereAuraBaseValue, HotMConfig.CONFIG.nonNectereAuraBaseValue)

        fun readFromPacket(buf: NetByteBuf): Sync2ClientData {
            val nectereAuraBaseValue = buf.readFloat()
            val nonNectereAuraBaseValue = buf.readFloat()
            return Sync2ClientData(nectereAuraBaseValue, nonNectereAuraBaseValue)
        }
    }

    fun writeToPacket(buf: NetByteBuf) {
        buf.writeFloat(nectereAuraBaseValue)
        buf.writeFloat(nonNectereAuraBaseValue)
    }
}