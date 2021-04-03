package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.ParentNetIdSingle
import alexiil.mc.lib.net.impl.ActiveMinecraftConnection
import alexiil.mc.lib.net.impl.McNetworkStack
import com.github.hotm.HotMConstants.str
import com.github.hotm.HotMRegistries
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.net.HotMNetwork
import com.github.hotm.util.DimBlockPos
import com.mojang.serialization.*
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import java.util.stream.Stream

interface AuraNode {
    companion object {
        private val LOGGER = LogManager.getLogger()

        val NET_ID = object : ParentNetIdSingle<AuraNode>(
            McNetworkStack.ROOT,
            AuraNode::class.java,
            str("aura_node"),
            -1
        ) {
            override fun readContext(buffer: NetByteBuf, ctx: IMsgReadCtx): AuraNode? {
                val mcConn = ctx.connection as ActiveMinecraftConnection
                val world = mcConn.player.world
                val access = StorageUtils.getAuraNetAccess(world)
                return access[buffer.readBlockPos()]
            }

            override fun writeContext(buffer: NetByteBuf, ctx: IMsgWriteCtx, value: AuraNode) {
                buffer.writeBlockPos(value.pos)
            }
        }

        private fun createDispatchCodec(
            access: AuraNetAccess, updateListener: Runnable, pos: BlockPos
        ): Codec<AuraNode> {
            return HotMRegistries.AURA_NODE_TYPE.dispatch(AuraNode::type) { type ->
                type.createCodec(access, updateListener, pos)
            }
        }

        fun createCodec(access: AuraNetAccess, updateListener: Runnable): Codec<AuraNode> {
            return object : MapCodec<AuraNode>() {
                override fun <T : Any?> keys(ops: DynamicOps<T>): Stream<T> {
                    return Stream.of(ops.createString("pos"), ops.createString("node"))
                }

                override fun <T : Any?> decode(
                    ops: DynamicOps<T>, input: MapLike<T>
                ): DataResult<AuraNode> {
                    val posElement = input.get("pos")
                        ?: return DataResult.error("Input does not contain aura node position [pos]: $input")

                    return BlockPos.CODEC.parse(ops, posElement).flatMap { pos ->
                        val nodeElement = input.get("node")
                            ?: return@flatMap DataResult.error("Input does not contain aura node [node]: $input")

                        createDispatchCodec(access, updateListener, pos).parse(ops, nodeElement)
                    }
                }

                override fun <T : Any?> encode(
                    input: AuraNode, ops: DynamicOps<T>, prefix: RecordBuilder<T>
                ): RecordBuilder<T> {
                    prefix.add(ops.createString("pos"), BlockPos.CODEC.encodeStart(ops, input.pos))
                    prefix.add(
                        ops.createString("node"),
                        createDispatchCodec(access, updateListener, input.pos).encodeStart(ops, input)
                    )
                    return prefix
                }
            }.codec()
        }

        fun fromPacket(access: AuraNetAccess, buf: NetByteBuf, ctx: IMsgReadCtx): AuraNode? {
            var nodePos = BlockPos(0, 0, 0)
            var nodeTypeId = -1
            var nodeTypeName: String? = "unknown"
            try {
                nodePos = buf.readBlockPos()
                nodeTypeId = buf.readInt()
                val nodeType = HotMNetwork.AURA_NET_TYPE_ID_CACHE.getObj(ctx.connection, nodeTypeId)

                if (nodeType == null) {
                    LOGGER.error(
                        "Unknown node type id: $nodeTypeId at: $nodePos! This chunk section will not be able to decode any more nodes"
                    )
                    return null
                }

                nodeTypeName = HotMRegistries.AURA_NODE_TYPE.getId(nodeType)?.toString()

                return nodeType.fromPacket(access, nodePos, buf, ctx)
            } catch (e: IndexOutOfBoundsException) {
                LOGGER.error("Error decoding node at: $nodePos id#: $nodeTypeId, id: $nodeTypeName", e)
                return null
            }
        }

        fun fromPacketNoPos(access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx): AuraNode? {
            var nodeTypeId = -1
            var nodeTypeName: String? = "unknown"
            try {
                nodeTypeId = buf.readInt()
                val nodeType = HotMNetwork.AURA_NET_TYPE_ID_CACHE.getObj(ctx.connection, nodeTypeId)

                if (nodeType == null) {
                    LOGGER.error(
                        "Unknown node type id: $nodeTypeId at: $pos! This chunk section will not be able to decode any more nodes"
                    )
                    return null
                }

                nodeTypeName = HotMRegistries.AURA_NODE_TYPE.getId(nodeType)?.toString()

                return nodeType.fromPacket(access, pos, buf, ctx)
            } catch (e: IndexOutOfBoundsException) {
                LOGGER.error("Error decoding node at: $pos id#: $nodeTypeId, id: $nodeTypeName", e)
                return null
            }
        }

        fun toPacket(node: AuraNode, buf: NetByteBuf, ctx: IMsgWriteCtx) {
            buf.writeBlockPos(node.pos)
            val nodeType = node.type
            val nodeTypeId = HotMNetwork.AURA_NET_TYPE_ID_CACHE.getId(ctx.connection, nodeType)
            buf.writeInt(nodeTypeId)

            node.writeToPacket(buf, ctx)
        }

        fun toPacketNoPos(node: AuraNode, buf: NetByteBuf, ctx: IMsgWriteCtx) {
            val nodeType = node.type
            val nodeTypeId = HotMNetwork.AURA_NET_TYPE_ID_CACHE.getId(ctx.connection, nodeType)
            buf.writeInt(nodeTypeId)

            node.writeToPacket(buf, ctx)
        }
    }

    val type: AuraNodeType<out AuraNode>

    val pos: BlockPos

    val dimPos: DimBlockPos

    fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx)

    fun onRemove() {}
}