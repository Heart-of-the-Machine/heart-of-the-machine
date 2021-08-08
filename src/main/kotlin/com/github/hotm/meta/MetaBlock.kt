package com.github.hotm.meta

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.ParentNetIdSingle
import alexiil.mc.lib.net.impl.ActiveMinecraftConnection
import alexiil.mc.lib.net.impl.McNetworkStack
import com.github.hotm.HotMConstants
import com.github.hotm.misc.HotMRegistries
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.net.HotMNetwork
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.meta.MetaAccess
import com.mojang.serialization.*
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import java.util.stream.Stream

interface MetaBlock {
    companion object {
        private val LOGGER = LogManager.getLogger()

        val NET_ID = object : ParentNetIdSingle<MetaBlock>(
            McNetworkStack.ROOT,
            MetaBlock::class.java,
            HotMConstants.str("meta_block"),
            -1
        ) {
            override fun readContext(buffer: NetByteBuf, ctx: IMsgReadCtx): MetaBlock? {
                val mcConn = ctx.connection as ActiveMinecraftConnection
                val world = mcConn.player.world
                val access = StorageUtils.getMetaAccess(world)
                return access[buffer.readBlockPos()]
            }

            override fun writeContext(buffer: NetByteBuf, ctx: IMsgWriteCtx, value: MetaBlock) {
                buffer.writeBlockPos(value.pos)
            }
        }

        private fun createDispatchCodec(
            access: MetaAccess, updateListener: Runnable, pos: BlockPos
        ): Codec<MetaBlock> {
            return HotMRegistries.META_BLOCK_TYPE.dispatch(MetaBlock::type) { type ->
                type.createCodec(access, updateListener, pos)
            }
        }

        fun createCodec(access: MetaAccess, updateListener: Runnable): Codec<MetaBlock> {
            return object : MapCodec<MetaBlock>() {
                override fun <T : Any?> keys(ops: DynamicOps<T>): Stream<T> {
                    return Stream.of(ops.createString("pos"), ops.createString("node"))
                }

                override fun <T : Any?> decode(
                    ops: DynamicOps<T>, input: MapLike<T>
                ): DataResult<MetaBlock> {
                    val posElement = input.get("pos")
                        ?: return DataResult.error("Input does not contain meta block position [pos]: $input")

                    return BlockPos.CODEC.parse(ops, posElement).flatMap { pos ->
                        val nodeElement = input.get("node")
                            ?: return@flatMap DataResult.error("Input does not contain meta block [node]: $input")

                        createDispatchCodec(access, updateListener, pos).parse(ops, nodeElement)
                    }
                }

                override fun <T : Any?> encode(
                    input: MetaBlock, ops: DynamicOps<T>, prefix: RecordBuilder<T>
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

        fun fromPacket(access: MetaAccess, buf: NetByteBuf, ctx: IMsgReadCtx): MetaBlock? {
            var nodePos = BlockPos(0, 0, 0)
            var nodeTypeId = -1
            var nodeTypeName: String? = "unknown"
            try {
                nodePos = buf.readBlockPos()
                nodeTypeId = buf.readInt()
                val nodeType = HotMNetwork.META_BLOCK_TYPE_ID_CACHE.getObj(ctx.connection, nodeTypeId)

                if (nodeType == null) {
                    LOGGER.error(
                        "Unknown node type id: $nodeTypeId at: $nodePos! This chunk section will not be able to decode any more nodes"
                    )
                    return null
                }

                nodeTypeName = HotMRegistries.META_BLOCK_TYPE.getId(nodeType)?.toString()

                return nodeType.fromPacket(access, nodePos, buf, ctx)
            } catch (e: IndexOutOfBoundsException) {
                LOGGER.error("Error decoding node at: $nodePos id#: $nodeTypeId, id: $nodeTypeName", e)
                return null
            }
        }

        fun fromPacketNoPos(access: MetaAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx): MetaBlock? {
            var nodeTypeId = -1
            var nodeTypeName: String? = "unknown"
            try {
                nodeTypeId = buf.readInt()
                val nodeType = HotMNetwork.META_BLOCK_TYPE_ID_CACHE.getObj(ctx.connection, nodeTypeId)

                if (nodeType == null) {
                    LOGGER.error(
                        "Unknown node type id: $nodeTypeId at: $pos! This chunk section will not be able to decode any more nodes"
                    )
                    return null
                }

                nodeTypeName = HotMRegistries.META_BLOCK_TYPE.getId(nodeType)?.toString()

                return nodeType.fromPacket(access, pos, buf, ctx)
            } catch (e: IndexOutOfBoundsException) {
                LOGGER.error("Error decoding node at: $pos id#: $nodeTypeId, id: $nodeTypeName", e)
                return null
            }
        }

        fun toPacket(node: MetaBlock, buf: NetByteBuf, ctx: IMsgWriteCtx) {
            buf.writeBlockPos(node.pos)
            val nodeType = node.type
            val nodeTypeId = HotMNetwork.META_BLOCK_TYPE_ID_CACHE.getId(ctx.connection, nodeType)
            buf.writeInt(nodeTypeId)

            node.writeToPacket(buf, ctx)
        }

        fun toPacketNoPos(node: MetaBlock, buf: NetByteBuf, ctx: IMsgWriteCtx) {
            val nodeType = node.type
            val nodeTypeId = HotMNetwork.META_BLOCK_TYPE_ID_CACHE.getId(ctx.connection, nodeType)
            buf.writeInt(nodeTypeId)

            node.writeToPacket(buf, ctx)
        }
    }

    val type: MetaBlockType<out MetaBlock>

    val pos: BlockPos

    val dimPos: DimBlockPos

    fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx)

    fun onRemove() {}
}