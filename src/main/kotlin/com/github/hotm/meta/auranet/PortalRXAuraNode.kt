package com.github.hotm.meta.auranet

import com.github.hotm.util.DimBlockPos

interface PortalRXAuraNode : AuraNode {
    fun isValid(): Boolean

    fun wouldCauseDependencyLoop(potentialAncestor: DimBlockPos, visitedNodes: MutableSet<DimBlockPos>): Boolean

    fun recalculateDescendants(visitedNodes: MutableSet<DimBlockPos>)
}