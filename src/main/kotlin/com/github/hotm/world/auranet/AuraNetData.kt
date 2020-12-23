package com.github.hotm.world.auranet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.apache.logging.log4j.LogManager

class AuraNetData(
    private val updateListener: Runnable,
    private var base: Float,
//    initialNodes: List<AuraNetNodeContainer>
) {
    companion object {
        val DEFAULT_BASE_VALUE = 1.0f

        private val LOGGER = LogManager.getLogger()

        fun createCodec(updateListener: Runnable): Codec<AuraNetData> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNetData> ->
                instance.group(
                    RecordCodecBuilder.point(updateListener),
                    Codec.FLOAT.fieldOf("base").forGetter(AuraNetData::base),
//                    AuraNetNodeContainer.createCodec(updateListener).listOf().fieldOf("nodes")
//                        .forGetter { ImmutableList.copyOf(it.nodesByPos.values) }
                ).apply(instance, ::AuraNetData)
            }
        }
    }

//    private val nodesByPos: Short2ObjectMap<AuraNetNodeContainer> = Short2ObjectOpenHashMap()

    var baseValue: Float
        get() = base
        set(value) {
            base = value
            updateListener.run()
        }

//    init {
//        initialNodes.forEach(::add)
//    }

    constructor(updateListener: Runnable) : this(updateListener, DEFAULT_BASE_VALUE/*, ImmutableList.of()*/)

//    fun add(pos: BlockPos, auraNetNode: AuraNetNode) {
//        if (add(AuraNetNodeContainer(pos, auraNetNode))) {
//            updateListener.run()
//        }
//    }
//
//    private fun add(container: AuraNetNodeContainer): Boolean {
//        val pos = container.pos
//        val index = ChunkSectionPos.packLocal(pos)
//        val curAuraNetNode = nodesByPos[index]
//        return if (curAuraNetNode != null) {
//            if (container.node.storageEquals(curAuraNetNode.node)) {
//                false
//            } else {
//                throw Util.throwOrPause(IllegalStateException("Aura Net Node data mismatch: already registered at $pos"))
//            }
//        } else {
//            nodesByPos[index] = container
//            true
//        }
//    }
//
//    operator fun get(pos: BlockPos): Optional<AuraNetNode> {
//        return Optional.ofNullable(nodesByPos[ChunkSectionPos.packLocal(pos)]?.node)
//    }
//
//    fun remove(pos: BlockPos) {
//        val container = nodesByPos[ChunkSectionPos.packLocal(pos)]
//        if (container == null) {
//            LOGGER.error("Aura Net Node data mismatch: never registered at $pos")
//        } else {
//            LOGGER.debug("Removed Aura Net Node at ${container.pos}")
//            updateListener.run()
//        }
//    }
}