package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos

interface SiphonAuraNode : AuraNode {

    /**
     * Determines whether adding making the source aura node a source in this node's chunk section would cause a
     * dependency loop.
     */
    fun wouldCauseDependencyLoop(potentialSource: DimBlockPos): Boolean {
        return wouldCauseDependencyLoop(potentialSource, hashSetOf())
    }

    /**
     * Recursive portion of the dependency loop checking mechanism.
     */
    fun wouldCauseDependencyLoop(potentialAncestor: DimBlockPos, visitedNodes: MutableSet<DimBlockPos>): Boolean

    /**
     * Asks this siphon to recalculate its value, usually due to a change in chunk aura.
     */
    fun recalculateSiphonValue(chunkAura: Float, siphonCount: Int, visitedNodes: MutableSet<DimBlockPos>)
}