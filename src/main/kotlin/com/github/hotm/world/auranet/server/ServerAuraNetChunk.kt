package com.github.hotm.world.auranet.server

import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.config.HotMConfig
import com.github.hotm.util.CodecUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.HotMDimensions
import com.github.hotm.auranet.AuraNode
import com.github.hotm.auranet.SiphonAuraNode
import com.github.hotm.auranet.SourceAuraNode
import com.google.common.collect.ImmutableList
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * Represents a 16x16x16 area containing nodes and a base aura.
 */
class ServerAuraNetChunk(
    val updateListener: Runnable,
    private var base: Float,
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
                    CodecUtils.PREFER_FLOAT_OR_INT.fieldOf("base").forGetter(ServerAuraNetChunk::base),
                    AuraNode.createCodec(storage, updateListener).listOf().fieldOf("nodes")
                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::ServerAuraNetChunk)
            }.orElseGet(Util.addPrefix("Failed to read Aura Net Data section: ", LOGGER::error)) {
                ServerAuraNetChunk(updateListener, dim)
            }
        }

        fun getBaseAura(dim: RegistryKey<World>): Float {
            return if (dim == HotMDimensions.NECTERE_KEY) {
                HotMConfig.CONFIG.nectereAuraBaseValue
            } else {
                HotMConfig.CONFIG.nonNectereAuraBaseValue
            }
        }

        fun toPacket(buf: NetByteBuf, ctx: IMsgWriteCtx, chunk: ServerAuraNetChunk) {
            // base aura
            buf.writeFloat(chunk.base)

            // node count
            val nodes = chunk.nodesByPos.values.toTypedArray()
            val nodesCount = nodes.size
            buf.writeVarUnsignedInt(nodesCount)

            if (nodesCount > 0) {
                val nodesBuf = NetByteBuf.buffer()
                for (node in nodes) {
                    AuraNode.toPacket(node, nodesBuf, ctx)
                }

                // nodes buf size
                val readableBytes = nodesBuf.readableBytes()
                buf.writeVarUnsignedInt(readableBytes)
                // nodes buf
                buf.writeBytes(nodesBuf)
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

    fun getBaseAura(): Float {
        return base
    }

    fun setBaseAura(baseValue: Float) {
        base = baseValue
        recalculateSiphons()
        updateListener.run()
    }

    fun put(auraNode: AuraNode) {
        val prevNode = putImpl(auraNode)
        updateListener.run()

        prevNode?.onRemove()

        if (prevNode is SourceAuraNode || prevNode is SiphonAuraNode
            || auraNode is SourceAuraNode || auraNode is SiphonAuraNode
        ) {
            recalculateSiphons()
        }
    }

    private fun putImpl(node: AuraNode): AuraNode? {
        val pos = node.pos
        val index = ChunkSectionPos.packLocal(pos)
        val curNode = nodesByPos[index]
        nodesByPos[index] = node

        LOGGER.debug("Set Aura Net node at $pos")
        return curNode
    }

    operator fun get(pos: BlockPos): AuraNode? {
        return nodesByPos[ChunkSectionPos.packLocal(pos)]
    }

    fun remove(pos: BlockPos) {
        val node = nodesByPos.remove(ChunkSectionPos.packLocal(pos))
        if (node == null) {
            LOGGER.error("Aura Net Node data mismatch: never registered at $pos")
        } else {
            node.onRemove()

            if (node is SourceAuraNode || node is SiphonAuraNode) {
                recalculateSiphons()
            }

            updateListener.run()

            LOGGER.debug("Removed Aura Net Node at ${node.pos}")
        }
    }

    fun getAllBy(filter: Predicate<AuraNode>): Stream<AuraNode> {
        return nodesByPos.values.stream().filter(filter)
    }

    fun getTotalAura(): Float {
        return base + nodesByPos.values.stream().filter { it is SourceAuraNode }
            .mapToDouble { (it as SourceAuraNode).getSourceAura().toDouble() }.sum().toFloat()
    }

    fun recalculateSiphons() {
        recalculateSiphons(hashSetOf())
    }

    fun recalculateSiphons(visitedNodes: MutableSet<DimBlockPos>) {
        val totalAura = getTotalAura()
        val siphons = nodesByPos.values.stream().filter { it is SiphonAuraNode }.toList()
        for (siphon in siphons) {
            (siphon as SiphonAuraNode).recalculateSiphonValue(totalAura, siphons.size, visitedNodes)
        }
    }

    fun updateAuraNodes(
        world: ServerWorld, storage: ServerAuraNetStorage,
        updater: ((BlockState, AuraNodeBlock, BlockPos) -> Unit) -> Unit
    ) {
        val removed = Short2ObjectOpenHashMap(nodesByPos)
        nodesByPos.clear()
        var updateSiphons = false
        var updateSave = false
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
                    val newNode = block.createAuraNode(state, world, storage, pos)
                    nodesByPos[index] = newNode

                    // oldNode is Siphon/Source checks are performed on the removed map instead of here
                    if (newNode is SiphonAuraNode || newNode is SourceAuraNode) {
                        updateSiphons = true
                    }

                    // Stuff has changed, might as well save the changes
                    updateSave = true
                }
            } else {
                // there didn't used to be a node block here
                val newNode = block.createAuraNode(state, world, storage, pos)
                nodesByPos[index] = newNode

                if (newNode is SiphonAuraNode || newNode is SourceAuraNode) {
                    updateSiphons = true
                }

                // Stuff has changed, might as well save the changes
                updateSave = true
            }
        }

        if (removed.isNotEmpty()) {
            updateSave = true
        }

        for (node in removed.values) {
            node.onRemove()

            if (node is SiphonAuraNode || node is SourceAuraNode) {
                updateSiphons = true
            }
        }

        if (updateSiphons) {
            recalculateSiphons()
        }

        if (updateSave) {
            updateListener.run()
        }
    }
}