package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos

/**
 * Represents a node that can have "parents" dependencies added to it or removed from it. When it
 * gets removed, it notifies its parents so that they can redistribute their aura.
 *
 * This interface should only be used server-side. Meaningful modifications here should be synced to clients by nodes
 * themselves.
 */
interface DependantAuraNode : AuraNode {
    /**
     * Determines whether the node is a suitable parent node for this node.
     */
    fun isParentValid(node: DependableAuraNode)

    /**
     * Determines whether making the parent node a parent of this node would cause a dependency loop.
     */
    fun wouldCauseDepencencyLoop(potentialParent: DependableAuraNode): Boolean {
        return wouldCauseDependencyLoop(potentialParent, hashSetOf())
    }

    /**
     * Recursive portion of the dependency loop checking mechanism.
     */
    fun wouldCauseDependencyLoop(potentialAncestor: DependableAuraNode, visitedNodes: MutableSet<AuraNode>): Boolean

    /**
     * Adds a parent node to this node's list of parent nodes. When this node gets removed from the world, it will
     * remove itself from its parents' lists of child nodes, allowing them to recalculate aura distribution.
     */
    fun addParent(node: DependableAuraNode)

    /**
     * Removes a parent node from this node's list of parent nodes.
     */
    fun removeParent(pos: DimBlockPos)

    /**
     * Adds a parent node to this node's list of parent nodes and prompts it to add this node to its list of children.
     */
    fun connectParent(pos: DimBlockPos)

    /**
     * Removes a parent node from this node's list of parent nodes and prompts it to remove this node from its list of
     * children.
     */
    fun disconnectParent(pos: DimBlockPos)

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
     * If a dependency loop is detected, the node that identified itself as part of the loop should remove itself and
     * its accompanying block, dropping items on the ground.
     */
    fun recalculateDescendants(visitedNodes: MutableSet<AuraNode>)
}