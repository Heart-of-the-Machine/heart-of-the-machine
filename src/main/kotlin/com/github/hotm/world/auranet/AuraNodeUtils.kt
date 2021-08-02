package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos

object AuraNodeUtils {
    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Float, siphonCount: Int): Float {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep)
    }

    inline fun <reified T> nodeAt(pos: BlockPos?, access: AuraNetAccess): T? {
        return pos?.let { access[it] as? T }
    }
}