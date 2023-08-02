package com.github.hotm.mod.util

import net.minecraft.registry.RegistryKey
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import com.kneelawk.graphlib.api.graph.GraphUniverse
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.util.NodePos

data class MaybeNode(val pos: DimPos, val holder: NodeHolder<BlockNode>?) {
    companion object {
        fun of(pos: DimPos): MaybeNode = MaybeNode(pos, null)

        fun of(holder: NodeHolder<BlockNode>): MaybeNode =
            MaybeNode(DimPos(holder.blockWorld.registryKey, holder.pos), holder)
    }

    val dim: RegistryKey<World>
        get() = pos.dim
    val nodePos: NodePos
        get() = pos.pos
    val blockPos: BlockPos
        get() = pos.blockPos
    val node: BlockNode
        get() = pos.node

    fun tryGetHolder(server: MinecraftServer, universe: GraphUniverse): NodeHolder<BlockNode>? {
        if (holder != null) return holder

        val world = server.getWorld(dim) ?: return null
        val view = universe.getServerGraphWorld(world)
        return view.getNodeAt(nodePos)
    }
}
