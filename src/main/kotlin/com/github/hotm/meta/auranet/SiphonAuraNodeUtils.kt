package com.github.hotm.meta.auranet

object SiphonAuraNodeUtils {
    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Float, siphonCount: Int): Float {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep)
    }
}