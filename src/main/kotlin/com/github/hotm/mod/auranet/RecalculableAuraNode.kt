package com.github.hotm.mod.auranet

interface RecalculableAuraNode {
    fun recalculateValue(getSiphonData: () -> SiphonChunkData)
}
