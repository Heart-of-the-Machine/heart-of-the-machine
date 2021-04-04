package com.github.hotm.world.auranet

object AuraNodeUtils {
    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Int, siphonCount: Int): Int {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep).toInt()
    }
}