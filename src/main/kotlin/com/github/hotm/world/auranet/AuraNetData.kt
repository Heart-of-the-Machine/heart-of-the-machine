package com.github.hotm.world.auranet

import com.github.hotm.HotMConfig
import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.blocks.DependableAuraNodeBlock
import com.github.hotm.blocks.SiphonAuraNodeBlock
import com.github.hotm.blocks.SourceAuraNodeBlock
import com.github.hotm.world.HotMDimensions
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
import java.util.concurrent.ExecutorService
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.collections.HashSet

class AuraNetData(
    private val updateListener: Runnable,
    private var base: Int,
    initialNodes: List<PositionedAuraNode>
) {
    companion object {
        private val LOGGER = LogManager.getLogger()

        fun createCodec(updateListener: Runnable, dim: RegistryKey<World>): Codec<AuraNetData> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNetData> ->
                instance.group(
                    RecordCodecBuilder.point(updateListener),
                    Codec.INT.fieldOf("base").forGetter(AuraNetData::base),
                    PositionedAuraNode.CODEC.listOf().fieldOf("nodes")
                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::AuraNetData)
            }.orElseGet(Util.method_29188("Failed to read Aura Net Data section: ", LOGGER::error)) {
                AuraNetData(updateListener, dim)
            }
        }

        fun getBaseAura(dim: RegistryKey<World>): Int {
            return if (dim == HotMDimensions.NECTERE_KEY) {
                HotMConfig.CONFIG.nectereAuraBaseValue
            } else {
                HotMConfig.CONFIG.nonNectereAuraBaseValue
            }
        }
    }

    private val nodesByPos: Short2ObjectMap<PositionedAuraNode> = Short2ObjectOpenHashMap()

    init {
        initialNodes.forEach(::set)
    }

    constructor(updateListener: Runnable, dim: RegistryKey<World>) : this(
        updateListener,
        getBaseAura(dim),
        ImmutableList.of()
    )

    fun getBaseAura(): Int {
        return base
    }

    fun setBaseAura(world: ServerWorld, storage: AuraNetStorage, baseValue: Int) {
        base = baseValue
        recalculateSiphons(world, storage)
        updateListener.run()
    }

    fun set(pos: BlockPos, auraNode: AuraNode) {
        if (set(PositionedAuraNode(pos, auraNode))) {
            updateListener.run()
        }
    }

    private fun set(container: PositionedAuraNode): Boolean {
        val pos = container.pos
        val index = ChunkSectionPos.packLocal(pos)
        val curContainer = nodesByPos[index]
        return if (curContainer != null && container.node.storageEquals(curContainer.node)) {
            false
        } else {
            nodesByPos[index] = container

            LOGGER.debug("Set Aura Net node at $pos")
            true
        }
    }

    operator fun get(pos: BlockPos): Optional<AuraNode> {
        return Optional.ofNullable(nodesByPos[ChunkSectionPos.packLocal(pos)]?.node)
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

    fun getAllBy(filter: Predicate<PositionedAuraNode>): Stream<PositionedAuraNode> {
        return nodesByPos.values.stream().filter(filter)
    }

    private fun recalculateSiphons(world: ServerWorld, storage: AuraNetStorage) {
        for (siphon in nodesByPos.values.stream().filter { it.node.block is SiphonAuraNodeBlock }) {
            val pos = siphon.pos
            val state = world.getBlockState(pos)
            val block = state.block
            if (block is AuraNodeBlock) {
                block.recalculate(state, world, storage, pos, HashSet())
            }
        }
    }

    fun updateAuraNodes(
        signalExecutor: ExecutorService,
        world: ServerWorld,
        updater: ((BlockState, PositionedAuraNode) -> Unit) -> Unit
    ) {
        // FIXME: Currently clears all node data on world load
        val missing = Short2ObjectOpenHashMap(nodesByPos)
        val updateeBlocks = Short2ObjectOpenHashMap<Block>()
        var updateSiphons = false
        nodesByPos.clear()
        updater { state, container ->
            val pos = container.pos
            val node = container.node

            val index = ChunkSectionPos.packLocal(pos)
            nodesByPos[index] = container

            if (missing.containsKey(index) && missing[index]!!.node.storageEquals(node)) {
                missing.remove(index)

                // we only want to add this block to the potential updatees if it's not being updated somewhere else
                updateeBlocks[index] = state.block
            } else {
                val tickScheduler = world.blockTickScheduler
                signalExecutor.execute {
                    tickScheduler.schedule(pos, state.block, 0)
                }

                if (node.block is SiphonAuraNodeBlock || node.block is SourceAuraNodeBlock) {
                    updateSiphons = true
                }
            }
        }

        for (container in missing.values) {
            val node = container.node
            val pos = container.pos
            val server = world.server
            val block = node.block
            if (block is DependableAuraNodeBlock) {
                signalExecutor.execute {
                    for (dependant in block.getDependants(world.getBlockState(pos), world, pos)) {
                        server.getWorld(dependant.dim)?.let { dimWorld ->
                            val state = dimWorld.getBlockState(dependant.pos)
                            dimWorld.blockTickScheduler.schedule(dependant.pos, state.block, 0)
                        }
                    }
                }
            }

            if (node.block is SiphonAuraNodeBlock || node.block is SourceAuraNodeBlock) {
                updateSiphons = true
            }
        }

        if (updateSiphons) {
            val tickScheduler = world.blockTickScheduler

            for (siphon in nodesByPos.values.stream().filter { it.node.block is SiphonAuraNodeBlock }) {
                val pos = siphon.pos
                val index = ChunkSectionPos.packLocal(pos)

                updateeBlocks[index]?.let { block ->
                    signalExecutor.execute {
                        tickScheduler.schedule(pos, block, 0)
                    }
                }
            }
        }
    }
}