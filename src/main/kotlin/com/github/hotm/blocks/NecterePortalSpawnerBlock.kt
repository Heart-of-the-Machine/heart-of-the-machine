package com.github.hotm.blocks

import com.github.hotm.HotMBlockEntities
import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class NecterePortalSpawnerBlock(settings: Settings) : BlockWithEntity(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return NecterePortalSpawnerBlockEntity(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type,
            HotMBlockEntities.NECTERE_PORTAL_SPAWNER_BLOCK_ENTITY,
            NecterePortalSpawnerBlockEntity.Companion::tick
        )
    }
}