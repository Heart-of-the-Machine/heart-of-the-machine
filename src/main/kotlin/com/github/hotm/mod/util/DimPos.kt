package com.github.hotm.mod.util

import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.util.NodePos

data class DimPos(val dim: RegistryKey<World>, val pos: NodePos) {
    companion object {
        fun of(holder: NodeHolder<*>): DimPos {
            return DimPos(holder.blockWorld.registryKey, holder.pos)
        }
    }

    val blockPos: BlockPos
        get() = pos.pos
    val node: BlockNode
        get() = pos.node
}
