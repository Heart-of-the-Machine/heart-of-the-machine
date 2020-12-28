package com.github.hotm.world.auranet

import com.github.hotm.HotMRegistries
import com.github.hotm.util.DimBlockPos
import com.mojang.serialization.Codec

interface AuraNode {
    companion object {
        val CODEC: Codec<AuraNode> = HotMRegistries.AURA_NODE_TYPE.dispatch(AuraNode::type, AuraNodeType::codec)
    }

    val type: AuraNodeType

    val dependenants: Collection<DimBlockPos>

    fun storageEquals(auraNode: AuraNode): Boolean
}