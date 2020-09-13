package com.github.hotm.blocks

import com.github.hotm.HotMBlockTags
import net.minecraft.block.BlockState
import net.minecraft.block.PlantBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

open class PlasseinPlantBlock(settings: Settings) : PlantBlock(settings) {
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return HotMBlockTags.PLASSEIN_FERTILE.contains(floor.block)
    }
}