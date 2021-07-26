package com.github.hotm.blockentity

import com.github.hotm.util.lazyVar
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

abstract class AbstractDependableAuraNodeBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state), RenderedDependableAuraNodeBlockEntity {

    /* Crown render variables */
    private var lastRenderWorldTime by lazyVar { world?.time ?: 0L }
    private var lastRenderTickDelta = 0f
    private var lastRenderDiff = 0f
    private val crownRolls = Object2FloatOpenHashMap<BlockPos>()

    override fun updateRenderValues(worldTime: Long, tickDelta: Float) {
        val dwt = worldTime - lastRenderWorldTime
        val dtd = tickDelta - lastRenderTickDelta
        lastRenderWorldTime = worldTime
        lastRenderTickDelta = tickDelta

        lastRenderDiff = dwt.toFloat() + dtd
    }

    override fun getAndUpdateCrownRoll(pos: BlockPos, rollSpeed: Float): Float {
        return crownRolls.addTo(pos, lastRenderDiff * rollSpeed)
    }
}