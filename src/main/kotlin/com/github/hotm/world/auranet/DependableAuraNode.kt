package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos

/**
 * Represents a node that can have "children" dependants added to it or removed from it. When it
 * recalculates its aura value, its contract is to notify its children with the aura value it's sending to them so they
 * can recalculate as well.
 *
 * This interface should only be used server-side. Meaningful modifications here should be synced to clients by nodes
 * themselves.
 */
interface DependableAuraNode : AuraNode {
    /**
     * Determines whether a child is a valid connection. This is *not* responsible for checking for recursion loops.
     */
    fun isChildValid(node: DependantAuraNode): Boolean

    /**
     * Adds a child node to this node's list of children. When this node gets removed from the world, it will remove
     * itself from its children's lists of parent nodes, allowing them to recalculate their aura values.
     */
    fun addChild(node: DependantAuraNode)

    /**
     * Removes a child node from this node's list of children.
     */
    fun removeChild(pos: BlockPos)

    /**
     * Called after a child has been added and had this node added to its list of parents.
     */
    fun onChildAdded(node: DependantAuraNode) {
    }

    /**
     * Called after a child has been removed and had this node removed from its list of parents.
     */
    fun onChildRemoved(pos: BlockPos) {
    }

    /**
     * Gets the aura this node supplies to the child aura node.
     */
    fun getSuppliedAura(child: DependantAuraNode): Int
}