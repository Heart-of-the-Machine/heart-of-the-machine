package com.github.hotm.blocks

import com.github.hotm.meta.MetaBlock
import com.github.hotm.meta.MetaBlockType
import com.github.hotm.world.meta.server.ServerMetaStorage
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface BlockWithMeta {
    val metaBlockType: MetaBlockType<out MetaBlock>

    /**
     * Called when this block is placed to create the associated meta block, **or** when a meta chunk fails to load and
     * its meta blocks must be regenerated.
     */
    fun createMetaBlock(state: BlockState, world: ServerWorld, storage: ServerMetaStorage, pos: BlockPos): MetaBlock
}