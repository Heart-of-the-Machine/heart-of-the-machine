package com.github.hotm.auranet

import com.github.hotm.blocks.HotMBlocks
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object PortalAuraNodeUtils {

    /**
     * Checks to see if the aura node's portal structure is valid.
     *
     * Notice: **This function causes chunk loading.** Use carefully.
     */
    fun isPortalStructureValid(pos: BlockPos, world: ServerWorld): Boolean {
        val portal = world.getBlockState(pos.down(2))
        return portal.block == HotMBlocks.NECTERE_PORTAL
    }
}