package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos
import java.util.stream.Stream

interface RenderedDependableAuraNode : DependableAuraNode {
    fun getChildrenForRender(): Stream<BlockPos>

    fun getSuppliedAuraForRender(pos: BlockPos): Int

    fun updateRenderValues(worldTime: Long, tickDelta: Float)

    fun getCrownRoll(pos: BlockPos): Float
}