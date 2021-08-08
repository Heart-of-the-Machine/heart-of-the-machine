package com.github.hotm.world.meta.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.world.meta.MetaAccess
import net.minecraft.util.math.ChunkPos

/**
 * Chunk pillar because Aura is stored in cubic chunks but Minecraft keeps track of pillar chunks.
 */
class ClientMetaChunkPillar private constructor(val pos: ChunkPos, val chunks: Array<ClientMetaChunk>) {
    companion object {
        fun fromPacket(
            access: MetaAccess,
            pos: ChunkPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): ClientMetaChunkPillar {
            val world = access.world
            val bitset = buf.readBitSet()
            val chunks = Array(world.topSectionCoord - world.bottomSectionCoord) { index ->
                if (bitset.get(index)) {
                    ClientMetaChunk.fromPacket(access, buf, ctx)
                } else {
                    ClientMetaChunk.createEmpty(access)
                }
            }

            return ClientMetaChunkPillar(pos, chunks)
        }
    }
}
