package com.github.hotm.blockentity

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class BasicSiphonAuraNodeBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractDependableAuraNodeBlockEntity(HotMBlockEntities.BASIC_SIPHON_AURA_NODE, pos, state)
