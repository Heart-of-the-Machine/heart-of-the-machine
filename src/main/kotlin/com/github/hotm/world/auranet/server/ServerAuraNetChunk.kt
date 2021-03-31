package com.github.hotm.world.auranet.server

import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConfig
import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.DependableAuraNode
import com.github.hotm.world.auranet.SiphonAuraNode
import com.github.hotm.world.auranet.SourceAuraNode
import com.google.common.collect.ImmutableList
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * Represents a 16x16x16 area containing nodes and a base aura.
 */
class ServerAuraNetChunk(
    private val updateListener: Runnable,
    private var base: Int,
    initialNodes: List<AuraNode>
) {
    companion object {
        private val LOGGER = LogManager.getLogger()

        fun createCodec(
            storage: ServerAuraNetStorage, updateListener: Runnable, dim: RegistryKey<World>
        ): Codec<ServerAuraNetChunk> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ServerAuraNetChunk> ->
                instance.group(
                    RecordCodecBuilder.point(updateListener),
                    Codec.INT.fieldOf("base").forGetter(ServerAuraNetChunk::base),
                    AuraNode.createCodec(storage, updateListener).listOf().fieldOf("nodes")
                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::ServerAuraNetChunk)
            }.orElseGet(Util.method_29188("Failed to read Aura Net Data section: ", LOGGER::error)) {
                ServerAuraNetChunk(updateListener, dim)
            }
        }

        fun getBaseAura(dim: RegistryKey<World>): Int {
            return if (dim == HotMDimensions.NECTERE_KEY) {
                HotMConfig.CONFIG.nectereAuraBaseValue
            } else {
                HotMConfig.CONFIG.nonNectereAuraBaseValue
            }
        }

        fun toPacket(
            buf: NetByteBuf, ctx: IMsgWriteCtx, chunkOptional: Optional<ServerAuraNetChunk>, dim: RegistryKey<World>
        ) {
            if (!chunkOptional.isPresent) {
                // base aura
                buf.writeVarUnsignedInt(getBaseAura(dim))
                // no nodes
                buf.writeVarUnsignedInt(0)
            } else {
                val chunk = chunkOptional.get()

                // base aura
                buf.writeVarUnsignedInt(chunk.base)

                val nodes = chunk.nodesByPos.values.toTypedArray()
                // node count
                buf.writeVarUnsignedInt(nodes.size)

                val nodesBuf = NetByteBuf.buffer()
                for (node in nodes) {
                    AuraNode.toPacket(node, nodesBuf, ctx)
                }

                // nodes buf size
                buf.writeVarUnsignedInt(nodesBuf.writerIndex())
                // nodes buf
                buf.writeByteArray(nodesBuf.array())
            }
        }
    }

    private val nodesByPos: Short2ObjectMap<AuraNode> = Short2ObjectOpenHashMap()

    init {
        initialNodes.forEach(::putImpl)
    }

    constructor(updateListener: Runnable, dim: RegistryKey<World>) : this(
        updateListener,
        getBaseAura(dim),
        ImmutableList.of()
    )

    fun getBaseAura(): Int {
        return base
    }

    fun setBaseAura(baseValue: Int) {
        base = baseValue
        recalculateSiphons()
        updateListener.run()
    }

    fun put(auraNode: AuraNode) {
        if (putImpl(auraNode)) {
            updateListener.run()
        }
    }

    private fun putImpl(node: AuraNode): Boolean {
        val pos = node.pos
        val index = ChunkSectionPos.packLocal(pos)
        val curNode = nodesByPos[index]
        return if (curNode != null && node.storageEquals(curNode)) {
            false
        } else {
            nodesByPos[index] = node

            LOGGER.debug("Set Aura Net node at $pos")
            true
        }
    }

    operator fun get(pos: BlockPos): AuraNode? {
        return nodesByPos[ChunkSectionPos.packLocal(pos)]
    }

    fun remove(pos: BlockPos) {
        val container = nodesByPos.remove(ChunkSectionPos.packLocal(pos))
        if (container == null) {
            LOGGER.error("Aura Net Node data mismatch: never registered at $pos")
        } else {
            LOGGER.debug("Removed Aura Net Node at ${container.pos}")
            updateListener.run()
        }
    }

    fun getAllBy(filter: Predicate<AuraNode>): Stream<AuraNode> {
        return nodesByPos.values.stream().filter(filter)
    }

    private fun recalculateSiphons() {
        for (siphon in nodesByPos.values.stream().filter { it is SiphonAuraNode }) {
            siphon.recalculate()
        }
    }

    fun updateAuraNodes(
        world: ServerWorld,
        updater: ((BlockState, AuraNodeBlock, BlockPos) -> Unit) -> Unit
    ) {
        // TODO: Fix recalculation function
        val removed = Short2ObjectOpenHashMap(nodesByPos)
        val updateeBlocks = Short2ObjectOpenHashMap<Block>()
        var updateSiphons = false
        nodesByPos.clear()
        updater { state, block, pos ->
            val type = block.auraNodeType
            val index = ChunkSectionPos.packLocal(pos)

            if (removed.containsKey(index)) {
                val oldNode = removed[index]!!
                if (oldNode.type == type) {
                    // the block is of the same type so no change took place
                    nodesByPos[index] = oldNode
                    removed.remove(index)
                } else {
                    // the node's block has changed since we last loaded
                    val newNode = block.createAuraNode(state, world, pos)
                    nodesByPos[index] = newNode

                    // oldNode is Siphon/Source checks are performed on the removed map instead of here
                    if (newNode is SiphonAuraNode || newNode is SourceAuraNode) {
                        updateSiphons = true
                    }
                }
            } else {
                // there didn't used to be a node block here
                val newNode = block.createAuraNode(state, world, pos)
                nodesByPos[index] = newNode

                if (newNode is SiphonAuraNode || newNode is SourceAuraNode) {
                    updateSiphons = true
                }
            }
        }

//        for (node in removed.values) {
//            val server = world.server
//            if (node is DependableAuraNode) {
//                for (dependant in node.getDependants()) {
//                    dependant.getAuraNode(server)?.recalculate()
//                }
//            }
//
//            if (node is SiphonAuraNode || node is SourceAuraNode) {
//                updateSiphons = true
//            }
//        }
//
//        if (updateSiphons) {
//            val tickScheduler = world.blockTickScheduler
//
//            for (siphon in nodesByPos.values.stream().filter { it.node.block is SiphonAuraNodeBlock }) {
//                val pos = siphon.pos
//                val index = ChunkSectionPos.packLocal(pos)
//
//                updateeBlocks[index]?.let { block ->
//                    signalExecutor.execute {
//                        tickScheduler.schedule(pos, block, 0)
//                    }
//                }
//            }
//        }
    }
}