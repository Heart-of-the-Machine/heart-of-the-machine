package com.github.hotm.world.auranet

interface SiphonAuraNode : AuraNode {
    fun recalculateSiphonValue(chunkAura: Int, siphonCount: Int)
}