package com.github.hotm.blocks

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import java.lang.IllegalStateException
import java.util.*

abstract class AbstractAuraNodeBlock<Node : AuraNode>(settings: Settings) : Block(settings), AuraNodeBlock {
    /**
     * Creates this AuraNodeBlock's AuraNode. Normally called when this block is placed.
     */
    abstract fun createAuraNode(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ): Node

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        super.onStateReplaced(state, world, pos, newState, moved)

        if (!world.isClient) {
            val storage = StorageUtils.getAuraNetStorage(world as ServerWorld)
            storage.remove(pos)
        }
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        if (!world.isClient) {
            val storage = StorageUtils.getAuraNetStorage(world as ServerWorld)
            storage.set(pos, createAuraNode(state, world, pos, oldState, notify))
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        updateAll(state, world, pos)
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        notify: Boolean
    ) {
        if (!world.isClient) {
            updateAll(state, world as ServerWorld, pos)
        }
    }

    protected fun checkCycles(serverWorld: ServerWorld, pos: BlockPos, previousNodes: Set<DimBlockPos>) {
        if (previousNodes.contains(DimBlockPos(serverWorld.registryKey, pos))) {
            handleCycle(serverWorld, pos)
        }
    }

    open fun handleCycle(serverWorld: ServerWorld, pos: BlockPos) {
        serverWorld.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 3.0f, Explosion.DestructionType.BREAK)
    }

    protected fun reconnectDescendants(serverWorld: ServerWorld, pos: BlockPos) {
        val storage = StorageUtils.getAuraNetStorage(serverWorld)
        val node = storage[pos].orElseThrow { IllegalStateException("Missing aura node for aura node block at $pos") }
        // TODO finish descendant reconnection logic
    }

    protected fun recalculateDescendants(serverWorld: ServerWorld, pos: BlockPos) {

    }
}