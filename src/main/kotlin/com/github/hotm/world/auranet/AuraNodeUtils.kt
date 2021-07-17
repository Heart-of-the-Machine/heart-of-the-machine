package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos

object AuraNodeUtils {
    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Int, siphonCount: Int): Int {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep).toInt()
    }

    inline fun <reified T> nodeAt(pos: BlockPos?, access: AuraNetAccess): T? {
        return pos?.let { access[it] as? T }
    }
}