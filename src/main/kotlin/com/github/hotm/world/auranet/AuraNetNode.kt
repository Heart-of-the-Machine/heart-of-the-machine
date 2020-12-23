package com.github.hotm.world.auranet

import com.github.hotm.HotMRegistries
import com.github.hotm.util.DimBlockPos
import com.mojang.serialization.Codec
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.function.Function

interface AuraNetNode {
    companion object {
        val CODEC: Codec<AuraNetNode> = HotMRegistries.AURA_NET_NODE.dispatch(AuraNetNode::codec, Function.identity())
    }

    val codec: Codec<out AuraNetNode>

    fun storageEquals(auraNetNode: AuraNetNode): Boolean

    fun recalculate(world: ServerWorld, pos: BlockPos): AuraNetNode

    fun dependenants(world: ServerWorld, pos: BlockPos): Collection<DimBlockPos>
}