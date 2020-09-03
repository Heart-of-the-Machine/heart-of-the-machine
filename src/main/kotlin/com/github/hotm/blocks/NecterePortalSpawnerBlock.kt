package com.github.hotm.blocks

import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

class NecterePortalSpawnerBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity {
        return NecterePortalSpawnerBlockEntity()
    }
}