package com.github.hotm.blocks

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.BasicAuraNode
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BasicAuraNodeBlock(settings: Settings) : Block(settings) {
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
            storage.set(pos, BasicAuraNode(1.0f))
        }
    }
}