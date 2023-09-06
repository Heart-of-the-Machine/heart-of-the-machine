package com.github.hotm.mod.node.aura

import java.util.function.Supplier
import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.auranet.CollectorDistributorAuraNode
import net.minecraft.nbt.NbtElement
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.graph.user.BlockNodeType
import com.kneelawk.graphlib.api.graph.user.NodeEntity
import com.kneelawk.graphlib.api.util.HalfLink

object CollectorDistributorAuraBlockNode : BlockNode {
    val TYPE = BlockNodeType.of(id("collector_distributor_aura_node"), Supplier { CollectorDistributorAuraBlockNode })

    override fun getType(): BlockNodeType = TYPE

    override fun toTag(): NbtElement? = null

    override fun findConnections(self: NodeHolder<BlockNode>): Collection<HalfLink> = emptyList()

    override fun canConnect(self: NodeHolder<BlockNode>, other: HalfLink): Boolean = false

    override fun onConnectionsChanged(self: NodeHolder<BlockNode>) {}

    override fun shouldHaveNodeEntity(self: NodeHolder<BlockNode>): Boolean = true

    override fun createNodeEntity(self: NodeHolder<BlockNode>): NodeEntity = CollectorDistributorAuraNode(0f)
}
