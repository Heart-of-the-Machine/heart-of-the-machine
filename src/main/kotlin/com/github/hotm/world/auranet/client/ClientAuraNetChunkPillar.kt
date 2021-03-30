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
        private const val MIN_CHUNK_Y = 0
        private const val MAX_CHUNK_Y = 15

        fun fromPacket(access: AuraNetAccess, pos: ChunkPos, buf: NetByteBuf, ctx: IMsgReadCtx): ClientAuraNetChunkPillar {
            val chunks = Array(MAX_CHUNK_Y - MIN_CHUNK_Y + 1) { y ->
                val sectionPos = ChunkSectionPos.from(pos, y)
                ClientAuraNetChunk.fromPacket(access, sectionPos, buf, ctx)
            }

            return ClientAuraNetChunkPillar(pos, chunks)
        }
    }
}
