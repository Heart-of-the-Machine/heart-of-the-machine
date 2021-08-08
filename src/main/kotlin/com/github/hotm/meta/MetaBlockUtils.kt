package com.github.hotm.meta

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.meta.MetaAccess
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object MetaBlockUtils {
    inline fun <reified T> getAt(pos: BlockPos?, world: World): T? {
        return getAt(pos, StorageUtils.getMetaAccess(world))
    }

    inline fun <reified T> getAt(pos: BlockPos?, access: MetaAccess): T? {
        return pos?.let { access[it] as? T }
    }
}