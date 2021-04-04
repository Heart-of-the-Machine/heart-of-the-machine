package com.github.hotm.blocks

import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface AuraNodeBlock {
    val auraNodeType: AuraNodeType<out AuraNode>

    /**
     * Called when this block is placed to create the associated aura node, **or** when an aura net chunk fails to load and
     * its aura nodes must be regenerated.
     */
    fun createAuraNode(state: BlockState, world: ServerWorld, storage: ServerAuraNetStorage, pos: BlockPos): AuraNode
}