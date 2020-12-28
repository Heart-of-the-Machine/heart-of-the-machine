package com.github.hotm.world.auranet

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.HotMRegistries
import net.minecraft.block.BlockState
import net.minecraft.util.registry.Registry

object AuraNodes {
    private val BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR = mutableMapOf<BlockState, () -> AuraNode>()

    val BASIC_TYPE = AuraNodeType(BasicAuraNode.CODEC)

    fun register() {
        register("basic", BASIC_TYPE, HotMBlocks.BASIC_AURA_NODE.stateManager.states) { BasicAuraNode(0) }
    }

    fun register(id: String, type: AuraNodeType, states: Collection<BlockState>, constructor: () -> AuraNode) {
        Registry.register(HotMRegistries.AURA_NODE_TYPE, HotMConstants.identifier(id), type)
        for (state in states) {
            BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR[state] = constructor
        }
    }

    fun containsState(blockState: BlockState): Boolean {
        return BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR.containsKey(blockState)
    }

    fun tryCreateAuraNode(blockState: BlockState): AuraNode? {
        return BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR[blockState]?.invoke()
    }
}