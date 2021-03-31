package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos

/**
 * Represents a node that can have "parents" dependencies added to it or removed from it. When it
 * gets removed, it notifies its parents so that they can redistribute their aura.
 *
 * This interface should only be used server-side. Meaningful modifications here should be synced to clients by nodes
 * themselves.
 */
interface DependantAuraNode {
    /**
     * Determines whether the node is a suitable parent node for this node.
     */
    fun isParentValid(node: DependableAuraNode)

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
}