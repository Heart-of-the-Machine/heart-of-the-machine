package com.github.hotm.blockentity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager

class DebugTickingBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HotMBlockEntities.DEBUG_TICKING, pos, state) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    fun tick() {
        val world = world
        if (world != null && !world.isClient) {
            LOGGER.info("[DEBUG] Tick")
        }
    }
}