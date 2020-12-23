package com.github.hotm.world.auranet

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.HotMRegistries
import net.minecraft.block.BlockState
import net.minecraft.util.registry.Registry

object AuraNetNodes {
    val BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR = mutableMapOf<BlockState, () -> AuraNetNode>()

    fun register() {
        Registry.register(HotMRegistries.AURA_NET_NODE, HotMConstants.identifier("basic"), BasicAuraNode.CODEC)
        BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR[HotMBlocks.BASIC_AURA_NODE.defaultState] = { BasicAuraNode(1.0f) }
    }
}