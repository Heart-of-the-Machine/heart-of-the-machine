package com.github.hotm.world.auranet.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.world.auranet.AuraNetAccess
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos

/**
 * Chunk pillar because Aura is stored in cubic chunks but Minecraft keeps track of pillar chunks.
 */
class ClientAuraNetChunkPillar private constructor(val pos: ChunkPos, val chunks: Array<ClientAuraNetChunk>) {
    companion object {
        fun fromPacket(
            access: AuraNetAccess,
            pos: ChunkPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): ClientAuraNetChunkPillar {
            val world = access.world
            val bitset = buf.readBitSet()
            val chunks = Array(world.topSectionCoord - world.bottomSectionCoord) { index ->
                if (bitset.get(index)) {
                    ClientAuraNetChunk.fromPacket(access, buf, ctx)
                } else {
                    ClientAuraNetChunk.createEmpty(access)
                }
            }

            return ClientAuraNetChunkPillar(pos, chunks)
        }
    }
}
