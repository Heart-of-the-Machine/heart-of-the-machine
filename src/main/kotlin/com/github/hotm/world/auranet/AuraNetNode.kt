package com.github.hotm.world.auranet

import com.github.hotm.HotMRegistries
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos

interface AuraNetNode {
    companion object {
        fun createCodec(updateListener: Runnable): Codec<AuraNetNode> {
            return HotMRegistries.AURA_NET_NODE.dispatch(AuraNetNode::codec) { it(updateListener) }
        }
    }

    val codec: (Runnable) -> Codec<out AuraNetNode>

    fun getPos(): BlockPos

    fun storageEquals(auraNetNode: AuraNetNode): Boolean

    fun recalculate()
}