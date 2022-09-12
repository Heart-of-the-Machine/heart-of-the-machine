package com.github.hotm.blocks

import com.github.hotm.misc.HotMBlockTags
import net.minecraft.block.BlockState
import net.minecraft.block.PlantBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

open class PlasseinPlantBlock(settings: Settings) : PlantBlock(settings) {
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return floor.isIn(HotMBlockTags.PLASSEIN_FERTILE)
    }
}
