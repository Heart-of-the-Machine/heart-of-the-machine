package com.github.hotm.mod.auranet

interface ConnectableAuraNode : AuraNode {
    companion object {
        // The smallest maximum distance two nodes can have
        const val MIN_CONNECT_DISTANCE = 64f
    }

    val connectDistanceExtension: Float
        get() = 0f
}
