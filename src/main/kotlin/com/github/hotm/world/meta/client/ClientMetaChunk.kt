package com.github.hotm.world.meta.client

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.net.sync.ClientSync2ClientData
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.meta.MetaAccess
import com.github.hotm.meta.MetaBlock
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import java.util.function.Predicate
import java.util.stream.Stream

class ClientMetaChunk private constructor(
    var baseAura: () -> Float, private val nodesByPos: Short2ObjectMap<MetaBlock>
) {
    companion object {
        fun fromPacket(
            access: MetaAccess, buf: NetByteBuf, ctx: IMsgReadCtx
        ): ClientMetaChunk {
            val baseAura = buf.readFloat()

            val nodesByPos = Short2ObjectOpenHashMap<MetaBlock>()
            val nodeCount = buf.readVarUnsignedInt()

            if (nodeCount > 0) {
                // make more resilient against invalid node ids and misbehaving node decoders
                val nodesBufSize = buf.readVarUnsignedInt()
                val nodesBuf = buf.readBytes(nodesBufSize)
                for (i in 0 until nodeCount) {
                    val node = MetaBlock.fromPacket(access, nodesBuf, ctx) ?: break
                    nodesByPos[ChunkSectionPos.packLocal(node.pos)] = node
                }
            }

            return ClientMetaChunk({ baseAura }, nodesByPos)
        }

        fun createEmpty(access: MetaAccess): ClientMetaChunk {
            return ClientMetaChunk(getBaseAura(access.world.registryKey), Short2ObjectOpenHashMap())
        }

        private fun getBaseAura(dim: RegistryKey<World>): () -> Float {
            return if (dim == HotMDimensions.NECTERE_KEY) {
                { ClientSync2ClientData.DATA.nectereAuraBaseValue }
            } else {
                { ClientSync2ClientData.DATA.nectereAuraBaseValue }
            }
        }
    }

    fun receivePut(access: MetaAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx) {
        MetaBlock.fromPacketNoPos(access, pos, buf, ctx)?.let { node ->
            nodesByPos[ChunkSectionPos.packLocal(node.pos)] = node
        }
    }

    fun receiveRemove(pos: BlockPos) {
        nodesByPos.remove(ChunkSectionPos.packLocal(pos))
    }

    fun getNode(pos: BlockPos): MetaBlock? {
        return nodesByPos[ChunkSectionPos.packLocal(pos)]
    }

    fun getAllBy(filter: Predicate<MetaBlock>): Stream<MetaBlock> {
        return nodesByPos.values.stream().filter(filter)
    }
}