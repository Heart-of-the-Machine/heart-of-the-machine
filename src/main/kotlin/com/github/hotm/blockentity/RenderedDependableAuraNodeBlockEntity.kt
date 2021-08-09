package com.github.hotm.blockentity

import net.minecraft.util.math.BlockPos

interface RenderedDependableAuraNodeBlockEntity {
    fun updateRenderValues(worldTime: Long, tickDelta: Float)

    fun getAndUpdateCrownRoll(pos: BlockPos, rollSpeed: Float): Float
}