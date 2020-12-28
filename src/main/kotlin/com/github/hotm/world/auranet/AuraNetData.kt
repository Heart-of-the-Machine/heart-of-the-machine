package com.github.hotm.world.auranet

import com.github.hotm.HotMConfig
import com.github.hotm.world.HotMDimensions
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
import java.util.*
import java.util.concurrent.ExecutorService

class AuraNetData(
    private val updateListener: Runnable,
    private var base: Int,
    initialNodes: List<AuraNodeContainer>
) {
    companion object {
        private val LOGGER = LogManager.getLogger()

        fun createCodec(updateListener: Runnable, dim: RegistryKey<World>): Codec<AuraNetData> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNetData> ->
                instance.group(
                    RecordCodecBuilder.point(updateListener),
                    Codec.INT.fieldOf("base").forGetter(AuraNetData::base),
                    AuraNodeContainer.CODEC.listOf().fieldOf("nodes")
                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::AuraNetData)
            }.orElseGet(Util.method_29188("Failed to read Aura Net Data section: ", LOGGER::error)) {
                AuraNetData(updateListener, dim)
            }
        }

        fun getBaseValue(dim: RegistryKey<World>): Int {
            return if (dim == HotMDimensions.NECTERE_KEY) {
                HotMConfig.CONFIG.nectereAuraBaseValue
            } else {
                HotMConfig.CONFIG.nonNectereAuraBaseValue
            }
        }
    }

    private val nodesByPos: Short2ObjectMap<AuraNodeContainer> = Short2ObjectOpenHashMap()

    var baseValue: Int
        get() = base
        set(value) {
            base = value
            updateListener.run()
        }

    init {
        initialNodes.forEach(::set)
    }

    constructor(updateListener: Runnable, dim: RegistryKey<World>) : this(
        updateListener,
        getBaseValue(dim),
        ImmutableList.of()
    )

    fun set(pos: BlockPos, auraNode: AuraNode) {
        if (set(AuraNodeContainer(pos, auraNode))) {
            updateListener.run()
        }
    }

    private fun set(container: AuraNodeContainer): Boolean {
        val pos = container.pos
        val index = ChunkSectionPos.packLocal(pos)
        val curContainer = nodesByPos[index]
        return if (curContainer != null && container.node.storageEquals(curContainer.node)) {
            false
        } else {
            nodesByPos[index] = container
            LOGGER.debug("Added Aura Net node at $pos")
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

    fun updateAuraNodes(
        signalExecutor: ExecutorService,
        world: ServerWorld,
        sectionPos: ChunkSectionPos,
        updater: ((BlockPos, BlockState, AuraNode) -> Unit) -> Unit
    ) {
        val missing = Short2ObjectOpenHashMap(nodesByPos)
        nodesByPos.clear()
        updater { pos, state, node ->
            val index = ChunkSectionPos.packLocal(pos)
            nodesByPos[index] = AuraNodeContainer(pos, node)

            if (missing.containsKey(index) && missing[index]!!.node.storageEquals(node)) {
                missing.remove(index)
            } else {
                val tickScheduler = world.blockTickScheduler
                signalExecutor.submit { tickScheduler.schedule(pos, state.block, 1) }
            }
        }

        for ((index, container) in missing) {
            val pos = sectionPos.unpackBlockPos(index)
            val node = container.node
            val server = world.server
            signalExecutor.submit {
                for (dependant in node.dependenants) {
                    server.getWorld(dependant.dim)?.let { world ->
                        val state = world.getBlockState(pos)
                        world.blockTickScheduler.schedule(pos, state.block, 1)
                    }
                }
            }
        }
    }
}