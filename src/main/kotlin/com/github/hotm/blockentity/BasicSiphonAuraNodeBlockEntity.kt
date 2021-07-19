package com.github.hotm.blockentity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class BasicSiphonAuraNodeBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HotMBlockEntities.BASIC_SIPHON_AURA_NODE, pos, state)
