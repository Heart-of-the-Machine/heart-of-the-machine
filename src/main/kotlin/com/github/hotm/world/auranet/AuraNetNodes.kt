package com.github.hotm.world.auranet

import net.minecraft.block.BlockState

object AuraNetNodes {
    val BLOCK_STATE_TO_AURA_NET_NODE_CONSTRUCTOR = mutableMapOf<BlockState, () -> AuraNetNode>()

    fun register() {

    }
}