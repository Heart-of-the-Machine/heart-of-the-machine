package com.github.hotm.blocks

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.AttributeProviderAuraNode
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractAuraNodeBlock(settings: Settings) : Block(settings), AuraNodeBlock, AttributeProvider {
    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, to: AttributeList<*>) {
        val access = StorageUtils.getAuraNetAccess(world)
        (access[pos] as? AttributeProviderAuraNode)?.addAllAttributes(to)
    }

    override fun onStateReplaced(
        state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean
    ) {
        super.onStateReplaced(state, world, pos, newState, moved)

        if (!world.isClient) {
            world as ServerWorld
            val storage = StorageUtils.getServerAuraNetStorage(world)
            // removing on the server syncs all clients in range
            storage.remove(pos)
        }
    }

    override fun onBlockAdded(
        state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean
    ) {
        if (!world.isClient) {
            world as ServerWorld
            val storage = StorageUtils.getServerAuraNetStorage(world)
            // putting on the server also syncs to all clients in range
            storage.put(createAuraNode(state, world, storage, pos))
        }
    }
}