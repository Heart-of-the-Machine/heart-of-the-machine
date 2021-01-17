package com.github.hotm.blocks

import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.auranet.AuraNetStorage
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.BasicAuraNode
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class BasicAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlock(settings), SiphonAuraNodeBlock {
    override fun createAuraNode(state: BlockState, worldKey: RegistryKey<World>, pos: BlockPos): AuraNode {
        return BasicAuraNode(0)
    }

    override fun reconnect(
        state: BlockState,
        world: ServerWorld,
        storage: AuraNetStorage,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>
    ) {
    }

    override fun recalculate(
        state: BlockState,
        world: ServerWorld,
        storage: AuraNetStorage,
        pos: BlockPos,
        previousNodes: MutableSet<DimBlockPos>
    ) {
        println("[RECALCULATING] @ $pos")
        checkCycles(state, world, pos, previousNodes)

        val value = storage.calculateSiphonValue(pos, 10, 2)
        storage.set(pos, BasicAuraNode(value))
    }
}