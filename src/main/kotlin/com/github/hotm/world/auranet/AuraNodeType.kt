package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos

interface AuraNodeType<T : AuraNode> {
    /**
     * Handles decoding an existing aura node form a save file.
     */
    fun createCodec(access: AuraNetAccess, updateListener: Runnable, pos: BlockPos): Codec<T>

    /**
     * Handles decoding an existing aura node from a sync packet sent from the server to the client.
     */
    fun fromPacket(access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx): T
}