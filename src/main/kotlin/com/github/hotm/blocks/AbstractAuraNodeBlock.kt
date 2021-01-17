package com.github.hotm.blocks

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.auranet.AuraNetStorage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import java.util.*

abstract class AbstractAuraNodeBlock(settings: Settings) : Block(settings), AuraNodeBlock {
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

            if (this is DependableAuraNodeBlock) {
                for (dependant in getDependants(state, world, pos)) {
                    world.server.getWorld(dependant.dim)?.let { depWorld ->
                        val depState = depWorld.getBlockState(dependant.pos)
                        val block = depState.block
                        if (block is AuraNodeBlock) {
                            block.updateAll(depState, depWorld, StorageUtils.getAuraNetStorage(depWorld), dependant.pos)
                        }
                    }
                }
            }

            // FIXME: gross split logic for updating siphons
            if (this is SiphonAuraNodeBlock || this is SourceAuraNodeBlock) {
                recalculateSiphons(world, pos, hashSetOf())
            }
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
            storage.set(pos, createAuraNode(state, world.registryKey, pos))
            updateAll(state, world, StorageUtils.getAuraNetStorage(world), pos)

            // FIXME: gross split logic for updating siphons
            if (this is SiphonAuraNodeBlock || this is SourceAuraNodeBlock) {
                recalculateSiphons(world, pos, hashSetOf())
            }
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        updateAll(state, world, StorageUtils.getAuraNetStorage(world), pos)
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
            updateAll(state, world as ServerWorld, StorageUtils.getAuraNetStorage(world), pos)
        }
    }

    protected fun checkCycles(
        state: BlockState,
        serverWorld: ServerWorld,
        pos: BlockPos,
        previousNodes: Set<DimBlockPos>
    ) {
        if (previousNodes.contains(DimBlockPos(serverWorld.registryKey, pos))) {
            handleCycle(state, serverWorld, pos)
        }
    }

    open fun handleCycle(state: BlockState, serverWorld: ServerWorld, pos: BlockPos) {
        serverWorld.removeBlock(pos, false)
        serverWorld.createExplosion(
            null,
            pos.x.toDouble() + 0.5,
            pos.y.toDouble() + 0.5,
            pos.z.toDouble() + 0.5,
            1f,
            false,
            Explosion.DestructionType.BREAK
        )
        dropStacks(state, serverWorld, pos)
    }

    protected fun reconnectDependants(
        state: BlockState,
        serverWorld: ServerWorld,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>
    ) {
        forDependants(state, serverWorld, pos, previousNodes, AuraNodeBlock::reconnect)
    }

    protected fun recalculateDependants(
        state: BlockState,
        serverWorld: ServerWorld,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>
    ) {
        forDependants(state, serverWorld, pos, previousNodes, AuraNodeBlock::recalculate)
    }

    private inline fun forDependants(
        state: BlockState,
        serverWorld: ServerWorld,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>,
        action: AuraNodeBlock.(BlockState, ServerWorld, AuraNetStorage, BlockPos, MutableSet<DimBlockPos>) -> Unit
    ) {
        if (this is DependableAuraNodeBlock) {
            val dimBlockPos = DimBlockPos(serverWorld.registryKey, pos)
            previousNodes.add(dimBlockPos)

            for (dependant in getDependants(state, serverWorld, pos)) {
                val server = serverWorld.server
                server.getWorld(dependant.dim)?.let { depWorld ->
                    val depState = depWorld.getBlockState(dependant.pos)
                    val block = depState.block
                    if (block is AuraNodeBlock) {
                        block.action(
                            depState,
                            depWorld,
                            StorageUtils.getAuraNetStorage(depWorld),
                            dependant.pos,
                            previousNodes
                        )
                    }
                }
            }

            previousNodes.remove(dimBlockPos)
        }
    }

    protected fun recalculateSiphons(
        serverWorld: ServerWorld,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>
    ) {
        val dimBlockPos = DimBlockPos(serverWorld.registryKey, pos)
        previousNodes.add(dimBlockPos)

        val storage = StorageUtils.getAuraNetStorage(serverWorld)
        for (siphon in storage.getAllBy(ChunkSectionPos.from(pos)) { it.node.block is SiphonAuraNodeBlock }) {
            val siphonPos = siphon.pos
            if (siphonPos != pos) {
                val state = serverWorld.getBlockState(siphonPos)
                val block = state.block
                if (block is AuraNodeBlock) {
                    block.recalculate(state, serverWorld, storage, siphonPos, previousNodes)
                }
            }
        }

        previousNodes.remove(dimBlockPos)
    }
}