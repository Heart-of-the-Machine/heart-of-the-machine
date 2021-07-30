package com.github.hotm.blockentity

import com.github.hotm.util.lazyVar
import com.github.hotm.world.auranet.DependencyAuraNodeUtils
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractDependableAuraNodeBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state), RenderedDependableAuraNodeBlockEntity {

    companion object {
        fun tickServer(world: World, pos: BlockPos, state: BlockState, entity: AbstractDependableAuraNodeBlockEntity) {
            entity.updateConnections()
        }
    }

    private var lastConnectionUpdate by lazyVar { world?.time ?: 0L }

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

    override fun readNbt(nbt: NbtCompound) {
        lastConnectionUpdate = if (nbt.contains("lastConnectionUpdate")) {
            nbt.getLong("lastConnectionUpdate")
        } else {
            world?.time ?: 0L
        }
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val new = super.writeNbt(nbt)
        new.putLong("lastConnectionUpdate", lastConnectionUpdate)
        return new
    }

    fun updateConnections() {
        val world = world ?: return
        val time = world.time
        // only perform connection updates once a second
        if (time - lastConnectionUpdate >= 20) {
            lastConnectionUpdate = time
            DependencyAuraNodeUtils.updateConnections(world, pos)
        }
    }
}