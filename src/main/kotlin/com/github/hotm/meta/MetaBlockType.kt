package com.github.hotm.meta

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.world.meta.MetaAccess
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos

interface MetaBlockType<T : MetaBlock> {
    /**
     * Handles decoding an existing meta block form a save file.
     */
    fun createCodec(access: MetaAccess, updateListener: Runnable, pos: BlockPos): Codec<T>

    /**
     * Handles decoding an existing meta block from a sync packet sent from the server to the client.
     */
    fun fromPacket(access: MetaAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx): T
}