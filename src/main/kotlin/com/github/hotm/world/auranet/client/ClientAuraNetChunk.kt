package com.github.hotm.world.auranet.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.world.auranet.AuraNetAccess
import com.github.hotm.world.auranet.AuraNode
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import java.util.function.Predicate
import java.util.stream.Stream

class ClientAuraNetChunk private constructor(
    var baseAura: Float, private val nodesByPos: Short2ObjectMap<AuraNode>
) {
    companion object {
        fun fromPacket(
            access: AuraNetAccess, pos: ChunkSectionPos, buf: NetByteBuf, ctx: IMsgReadCtx
        ): ClientAuraNetChunk {
            val baseAura = buf.readFloat()

            val nodesByPos = Short2ObjectOpenHashMap<AuraNode>()
            val nodeCount = buf.readVarUnsignedInt()

            if (nodeCount > 0) {
                // make more resilient against invalid node ids and misbehaving node decoders
                val nodesBufSize = buf.readVarUnsignedInt()
                val nodesBuf = buf.readBytes(nodesBufSize)
                for (i in 0 until nodeCount) {
                    val node = AuraNode.fromPacket(access, nodesBuf, ctx) ?: break
                    nodesByPos[ChunkSectionPos.packLocal(node.pos)] = node
                }
            }

            return ClientAuraNetChunk(baseAura, nodesByPos)
        }
    }

    fun receivePut(access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx) {
        AuraNode.fromPacketNoPos(access, pos, buf, ctx)?.let { node ->
            nodesByPos[ChunkSectionPos.packLocal(node.pos)] = node
        }
    }

    fun receiveRemove(pos: BlockPos) {
        nodesByPos.remove(ChunkSectionPos.packLocal(pos))
    }

    fun getNode(pos: BlockPos): AuraNode? {
        return nodesByPos[ChunkSectionPos.packLocal(pos)]
    }

    fun getAllBy(filter: Predicate<AuraNode>): Stream<AuraNode> {
        return nodesByPos.values.stream().filter(filter)
    }
}