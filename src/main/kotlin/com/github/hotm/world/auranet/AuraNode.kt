package com.github.hotm.world.auranet

import com.github.hotm.HotMRegistries
import com.github.hotm.blocks.AuraNodeBlock
import com.mojang.serialization.Codec
import java.util.function.Function

interface AuraNode {
    companion object {
        val CODEC: Codec<AuraNode> = HotMRegistries.AURA_NODE_TYPE.dispatch(AuraNode::codec, Function.identity())
    }

    val codec: Codec<out AuraNode>

    /**
     * Used by AuraNetData to determine what blocks are updated if this aura node were to be removed during a save load.
     */
//    val dependants: Collection<DimBlockPos>

    val block: AuraNodeBlock

    fun storageEquals(auraNode: AuraNode): Boolean
}