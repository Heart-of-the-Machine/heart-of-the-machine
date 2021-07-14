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
        fun fromPacket(access: AuraNetAccess, pos: ChunkPos, buf: NetByteBuf, ctx: IMsgReadCtx): ClientAuraNetChunkPillar {
            val world = access.world
            val chunks = Array(world.topSectionCoord - world.bottomSectionCoord) { y ->
                val sectionPos = ChunkSectionPos.from(pos, y)
                ClientAuraNetChunk.fromPacket(access, sectionPos, buf, ctx)
            }

            return ClientAuraNetChunkPillar(pos, chunks)
        }
    }
}
