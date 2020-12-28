package com.github.hotm.blocks

import com.github.hotm.world.auranet.BasicAuraNode
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BasicAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlock<BasicAuraNode>(settings) {
    override fun createAuraNode(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ): BasicAuraNode {
        return BasicAuraNode(1)
    }
}