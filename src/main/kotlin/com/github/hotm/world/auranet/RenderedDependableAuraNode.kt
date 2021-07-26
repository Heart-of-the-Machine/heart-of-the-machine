package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos
import java.util.stream.Stream

interface RenderedDependableAuraNode : DependableAuraNode {
    fun getChildrenForRender(): Stream<BlockPos>

    fun getSuppliedAuraForRender(pos: BlockPos): Int

    fun getCrownRollSpeed(pos: BlockPos): Float
}