package com.github.hotm.world.auranet

import com.github.hotm.mixinapi.StorageUtils
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object AuraNodeUtils {
    fun calculateSiphonValue(initDenom: Float, denomStep: Float, chunkAura: Float, siphonCount: Int): Float {
        return chunkAura / (denomStep * siphonCount + initDenom - denomStep)
    }

    inline fun <reified T> nodeAt(pos: BlockPos?, world: World): T? {
        return nodeAt(pos, StorageUtils.getAuraNetAccess(world))
    }

    inline fun <reified T> nodeAt(pos: BlockPos?, access: AuraNetAccess): T? {
        return pos?.let { access[it] as? T }
    }
}