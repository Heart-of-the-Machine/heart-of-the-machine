package com.github.hotm.blockentity

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class CollectorDistributorAuraNodeBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractDependableAuraNodeBlockEntity(HotMBlockEntities.COLLECTOR_DISTRIBUTOR_AURA_NODE, pos, state)
