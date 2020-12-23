package com.github.hotm.world.auranet

import com.google.common.collect.ImmutableList
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import org.apache.logging.log4j.LogManager
import java.util.*

class AuraNetData(
    private val updateListener: Runnable,
    private var base: Float,
    initialNodes: List<AuraNetNodeContainer>
) {
    companion object {
        const val DEFAULT_BASE_VALUE = 1.0f

        private val LOGGER = LogManager.getLogger()

        fun createCodec(updateListener: Runnable): Codec<AuraNetData> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNetData> ->
                instance.group(
                    RecordCodecBuilder.point(updateListener),
                    Codec.FLOAT.fieldOf("base").forGetter(AuraNetData::base),
                    AuraNetNodeContainer.CODEC.listOf().fieldOf("nodes")
                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::AuraNetData)
            }.orElseGet(Util.method_29188("Failed to read Aura Net Data section: ", LOGGER::error)) {
                AuraNetData(updateListener)
            }
        }
    }

    private val nodesByPos: Short2ObjectMap<AuraNetNodeContainer> = Short2ObjectOpenHashMap()

    var baseValue: Float
        get() = base
        set(value) {
            base = value
            updateListener.run()
        }

    init {
        initialNodes.forEach(::set)
    }

    constructor(updateListener: Runnable) : this(updateListener, DEFAULT_BASE_VALUE, ImmutableList.of())

    fun set(pos: BlockPos, auraNetNode: AuraNetNode) {
        if (set(AuraNetNodeContainer(pos, auraNetNode))) {
            updateListener.run()
        }
    }

    private fun set(container: AuraNetNodeContainer): Boolean {
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

    operator fun get(pos: BlockPos): Optional<AuraNetNode> {
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
}