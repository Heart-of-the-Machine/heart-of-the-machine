package com.github.hotm.meta.auranet

import com.github.hotm.util.DimBlockPos
import net.minecraft.util.math.BlockPos

/**
 * Represents a node that can have "parents" dependencies added to it or removed from it. When it
 * gets removed, it notifies its parents so that they can redistribute their aura.
 *
 * This interface should only be used server-side. Meaningful modifications here should be synced to clients by nodes
 * themselves.
 */
interface DependantAuraNode : AuraNode {
    /**
     * Provides the maximum distance that this node is able to connect over. This is added to the child's maxDistance
     * to obtain the full max distance.
     */
    val maxDistance: Double

    /**
     * Determines whether the node is a suitable parent node for this node.
     */
    fun isParentValid(node: DependableAuraNode): Boolean

    /**
     * Determines whether making the parent node a parent of this node would cause a dependency loop.
     */
    fun wouldCauseDependencyLoop(potentialParent: DimBlockPos): Boolean {
        return wouldCauseDependencyLoop(potentialParent, hashSetOf())
    }

    /**
     * Recursive portion of the dependency loop checking mechanism.
     */
    fun wouldCauseDependencyLoop(potentialAncestor: DimBlockPos, visitedNodes: MutableSet<DimBlockPos>): Boolean

    /**
     * Adds a parent node to this node's list of parent nodes. When this node gets removed from the world, it will
     * remove itself from its parents' lists of child nodes, allowing them to recalculate aura distribution.
     */
    fun addParent(node: DependableAuraNode)

    /**
     * Removes a parent node from this node's list of parent nodes.
     */
    fun removeParent(pos: BlockPos)

    /**
     * Called after a parent node has been added and had this node added to its list of children.
     */
    fun onParentAdded(node: DependableAuraNode) {
    }

    /**
     * Called after a parent node has been removed and had this node removed from its list of children.
     */
    fun onParentRemoved(pos: BlockPos) {
    }

    /**
     * Causes the child node to recalculate its aura value and the aura values of all affected descendants.
     *
     * In this case, the child node will likely ask all parent nodes for their current supplied values to this node.
     */
    fun recalculateDescendants() {
        recalculateDescendants(hashSetOf())
    }

    /**
     * This handles the recursive portion of aura value recalculation.
     *
     * If a dependency loop is detected, the node that identified itself as part of the loop should break the connection
     * in some way, possibly by removing itself and its accompanying block, dropping items on the ground.
     */
    fun recalculateDescendants(visitedNodes: MutableSet<DimBlockPos>)
}