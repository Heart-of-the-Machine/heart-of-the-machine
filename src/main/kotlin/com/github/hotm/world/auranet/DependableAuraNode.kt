package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos

/**
 * Represents a node that can have "children" dependants added to it or removed from it. When it
 * recalculates its aura value, its contract is to notify its children with the aura value it's sending to them so they
 * can recalculate as well.
 *
 * This interface should only be used server-side. Meaningful modifications here should be synced to clients by nodes
 * themselves.
 */
interface DependableAuraNode {
    /**
     * Determines whether a child is a valid connection. This is responsible for checking for recursion loops.
     */
    fun isChildValid(node: DependantAuraNode)

    /**
     * Adds a child node to this node's list of children. When this node gets removed from the world, it will remove
     * itself from its children's lists of parent nodes, allowing them to recalculate their aura values.
     */
    fun addChild(node: DependantAuraNode)

    /**
     * Removes a child node from this node's list of children.
     */
    fun removeChild(pos: DimBlockPos)

    /**
     * Adds a child node to this node's list of children and prompts it to add this node to its list of parents.
     */
    fun connectChild(pos: DimBlockPos)

    /**
     * Gets the aura this node supplies to the child aura node.
     */
    fun getSuppliedAura(child: DependantAuraNode): Int
}