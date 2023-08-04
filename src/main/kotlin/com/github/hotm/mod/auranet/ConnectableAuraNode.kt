package com.github.hotm.mod.auranet

interface ConnectableAuraNode : AuraNode {
    companion object {
        // The smallest maximum distance two nodes can have
        const val MIN_CONNECT_DISTANCE = 64f

        fun isWithinRange(a: ConnectableAuraNode, b: ConnectableAuraNode): Boolean {
            val maxDistance = MIN_CONNECT_DISTANCE + a.connectDistanceExtension + b.connectDistanceExtension
            val maxDistanceSqr = maxDistance * maxDistance
            val distanceSqr = a.context.blockPos.getSquaredDistance(b.context.blockPos)
            return distanceSqr <= maxDistanceSqr
        }
    }

    val connectDistanceExtension: Float
        get() = 0f
}
