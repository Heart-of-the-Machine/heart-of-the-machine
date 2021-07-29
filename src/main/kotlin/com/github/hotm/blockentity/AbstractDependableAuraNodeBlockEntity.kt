package com.github.hotm.blockentity

import com.github.hotm.util.lazyVar
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
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
    private val visitedCrowns = ObjectOpenHashSet<BlockPos>()

    override fun updateRenderValues(worldTime: Long, tickDelta: Float) {
        val dwt = worldTime - lastRenderWorldTime
        val dtd = tickDelta - lastRenderTickDelta
        lastRenderWorldTime = worldTime
        lastRenderTickDelta = tickDelta

        lastRenderDiff = dwt.toFloat() + dtd

        // clean up unused crown rolls
        crownRolls.keys.retainAll(visitedCrowns)
        visitedCrowns.clear()
    }

    override fun getAndUpdateCrownRoll(pos: BlockPos, rollSpeed: Float): Float {
        val immutable = pos.toImmutable()
        visitedCrowns.add(immutable)
        return crownRolls.addTo(immutable, lastRenderDiff * rollSpeed)
    }
}