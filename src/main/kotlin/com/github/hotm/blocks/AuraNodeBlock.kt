package com.github.hotm.blocks

import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.auranet.AuraNetStorage
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

interface AuraNodeBlock {
    /**
     * Creates this AuraNodeBlock's AuraNode. Normally called when this block is placed.
     */
    fun createAuraNode(
        state: BlockState,
        worldKey: RegistryKey<World>,
        pos: BlockPos
    ): AuraNode

    fun updateAll(state: BlockState, world: ServerWorld, storage: AuraNetStorage, pos: BlockPos) {
        reconnect(state, world, storage, pos, hashSetOf())
        recalculate(state, world, storage, pos, hashSetOf())
    }

    fun reconnect(state: BlockState, world: ServerWorld, storage: AuraNetStorage, pos: BlockPos, previousNodes: MutableSet<DimBlockPos>)

    fun recalculate(state: BlockState, world: ServerWorld, storage: AuraNetStorage, pos: BlockPos, previousNodes: MutableSet<DimBlockPos>)
}