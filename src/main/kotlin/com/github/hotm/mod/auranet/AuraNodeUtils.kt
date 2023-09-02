package com.github.hotm.mod.auranet

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.auranet.ConnectableAuraNode.Companion.MIN_CONNECT_DISTANCE
import com.github.hotm.mod.item.TunerConnectResult
import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.node.aura.AuraLinkEntity
import com.github.hotm.mod.node.aura.AuraLinkKey
import com.github.hotm.mod.util.DimChunkSectionPos
import com.github.hotm.mod.util.DimPos
import com.github.hotm.mod.world.aura.Aura
import kotlin.math.sqrt
import kotlin.streams.asSequence
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkSectionPos
import com.kneelawk.graphlib.api.graph.GraphView
import com.kneelawk.graphlib.api.graph.GraphWorld
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode

object AuraNodeUtils {
    fun connect(world: GraphWorld, parent: ParentAuraNode, child: ChildAuraNode): TunerConnectResult {
        if (parent.context.blockWorld.registryKey != child.context.blockWorld.registryKey)
            return TunerConnectResult.WrongDimension(
                parent.context.blockWorld.registryKey,
                child.context.blockWorld.registryKey
            )

        val maxDistance = MIN_CONNECT_DISTANCE + parent.connectDistanceExtension + child.connectDistanceExtension
        val maxDistanceSqr = maxDistance * maxDistance
        val distanceSqr = parent.context.blockPos.getSquaredDistance(child.context.blockPos)
        if (distanceSqr > maxDistanceSqr)
            return TunerConnectResult.TooFar(sqrt(distanceSqr).toFloat(), maxDistance)

        if (wouldCauseLoop(parent.context.holder, child.context.holder))
            return TunerConnectResult.DependencyLoop

        parent.preAddChild(child)
        child.preAddParent(parent)

        world.connectNodes(parent.context.pos, child.context.pos, AuraLinkKey, AuraLinkEntity(parent.context.pos, 0f))

        updateValues(parent.context.holder)

        return TunerConnectResult.Success
    }

    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Float, siphonCount: Int): Float {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep)
    }

    fun getAllSiphons(view: GraphView, pos: ChunkSectionPos): Sequence<NodeHolder<BlockNode>> {
        return view.getAllGraphsInChunkSection(pos).asSequence().flatMap { it.getNodesInChunkSection(pos).asSequence() }
            .filter { it.nodeEntity is SiphonAuraNode }
    }

    fun calculateSources(view: GraphView, pos: ChunkSectionPos): Float {
        var sum = 0f
        for (graph in view.getAllGraphsInChunkSection(pos)) {
            for (node in graph.getNodesInChunkSection(pos)) {
                val entity = node.getNodeEntity(SourceAuraNode::class.java)
                if (entity != null) {
                    sum += entity.getSourceAura()
                }
            }
        }
        return sum
    }

    fun updateChunkAura(view: GraphView, pos: ChunkSectionPos) {
        val world = view.world as? ServerWorld
            ?: throw IllegalStateException("updateChunkAura should only be called on the logical server")
        recalculateChunkAura(world, view, pos)
        updateAllSiphons(view, pos)
    }

    fun updateAllSiphons(view: GraphView, pos: ChunkSectionPos) {
        val auraCache = mutableMapOf<DimChunkSectionPos, SiphonChunkData>()
        for (siphon in getAllSiphons(view, pos)) {
            updateValues(siphon, auraCache)
        }
    }

    fun updateValues(root: NodeHolder<*>, auraCache: MutableMap<DimChunkSectionPos, SiphonChunkData> = mutableMapOf()) {
        // do depth-first updates, making sure to watch out for loops

        val parentNodes = mutableSetOf<DimPos>()
        val stack = ArrayDeque<Node>()
        stack.addLast(Node(root, false))

        while (stack.isNotEmpty()) {
            val node = stack.last()
            val pos = DimPos.of(node.holder)
            val entity = node.holder.getNodeEntity(AuraNode::class.java)
            if (entity == null) {
                stack.removeLast()
                continue
            }

            if (!node.visited) {
                // check for loops
                if (parentNodes.contains(pos)) {
                    // just skip loops (if we're one of our own ancestors)
                    HotMLog.LOG.warn("Aura loop detected at ${pos}. Ignoring...")
                    stack.removeLast()
                    continue
                }
                parentNodes.add(pos)

                // visit node
                updateValue(entity, auraCache)

                // mark visited
                node.visited = true

                // add children or pop if no children found
                val children = getAffectedNodes(entity)
                if (children.isNotEmpty()) {
                    for (child in children) {
                        stack.addLast(Node(child, false))
                    }
                } else {
                    // this node has no children, so we're done with it
                    stack.removeLast()
                    parentNodes.remove(pos)
                }
            } else {
                // we've visited all this node's descendents, so we're done with it
                stack.removeLast()
                parentNodes.remove(pos)
            }
        }
    }

    fun wouldCauseLoop(potentialParent: NodeHolder<BlockNode>, potentialChild: NodeHolder<BlockNode>): Boolean {
        // do a breadth-first search of the graph for the potential parent

        val parentPos = DimPos.of(potentialParent)

        val visitedNodes = mutableSetOf<DimPos>()
        val queue = ArrayDeque<NodeHolder<BlockNode>>()
        queue.addFirst(potentialChild)

        while (queue.isNotEmpty()) {
            val node = queue.removeLast()

            // prevent looping forever if a loop does happen to exist already (also prevents redundant checks)
            val pos = DimPos.of(node)
            if (visitedNodes.contains(pos)) continue
            visitedNodes.add(pos)

            val entity = node.getNodeEntity(AuraNode::class.java) ?: continue

            // visit node
            if (pos == parentPos) {
                return true
            }

            for (child in getAffectedNodes(entity)) {
                queue.addFirst(child)
            }
        }

        return false
    }

    private fun getAffectedNodes(node: AuraNode): Collection<NodeHolder<BlockNode>> {
        val server = node.context.blockWorld.server
            ?: throw IllegalStateException("getAffectedNodes should only be called on the logical server")
        val distinct = mutableSetOf<DimPos>()
        val nodes = mutableListOf<NodeHolder<BlockNode>>()

        if (node is ParentAuraNode) {
            for (child in node.getChildNodes()) {
                child.tryGetHolder(server, HotMUniverses.NETWORKS)?.let {
                    val pos = DimPos.of(it)
                    if (!distinct.contains(pos)) {
                        distinct.add(pos)
                        nodes.add(it)
                    }
                }
            }
        }

        if (node is SourceAuraNode) {
            val sectionPos = ChunkSectionPos.from(node.context.blockPos)
            for (siphon in getAllSiphons(node.context.graphWorld, sectionPos)) {
                val pos = DimPos.of(siphon)
                if (!distinct.contains(pos)) {
                    distinct.add(pos)
                    nodes.add(siphon)
                }
            }
        }

        return nodes
    }

    private fun updateValue(node: AuraNode, auraCache: MutableMap<DimChunkSectionPos, SiphonChunkData>) {
        val world = node.context.blockWorld as? ServerWorld
            ?: throw IllegalStateException("updateValue should only be called on the logical server")

        if (node is RecalculableAuraNode) {
            node.recalculateValue {
                val pos = ChunkSectionPos.from(node.context.blockPos)
                val key = DimChunkSectionPos(world.registryKey, pos)
                auraCache.computeIfAbsent(key) { calculateAuraCacheEntry(world, node.context.graphWorld, pos) }
            }
        }

        // finally, update the chunk's aura
        if (node is SourceAuraNode) {
            recalculateChunkAura(world, node.context.graphWorld, ChunkSectionPos.from(node.context.blockPos))
        }
    }

    private fun recalculateChunkAura(world: ServerWorld, graphView: GraphView, chunkPos: ChunkSectionPos) {
        val base = Aura.getBase(world, chunkPos)
        Aura.update(world, chunkPos, base + calculateSources(graphView, chunkPos))
    }

    private fun calculateAuraCacheEntry(
        serverWorld: ServerWorld, graphView: GraphView, pos: ChunkSectionPos
    ): SiphonChunkData {
        val currentAura = Aura.get(serverWorld, pos)
        val siphonCount = getAllSiphons(graphView, pos).count()
        return SiphonChunkData(currentAura, siphonCount)
    }

    private data class Node(val holder: NodeHolder<*>, var visited: Boolean)
}
