package com.github.hotm.blockentity

import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.HotMConstants
import com.google.common.collect.Sets
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

object HotMBlockEntities {
    val NECTERE_PORTAL_SPAWNER_BLOCK_ENTITY =
        newBlockEntityType(::NecterePortalSpawnerBlockEntity, HotMBlocks.NECTERE_PORTAL_SPAWNER)

    fun register() {
        register(NECTERE_PORTAL_SPAWNER_BLOCK_ENTITY, "nectere_portal_spawner")
    }

    private fun register(type: BlockEntityType<*>, name: String) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, HotMConstants.identifier(name), type)
    }

    private fun <T : BlockEntity> newBlockEntityType(
        supplier: (BlockPos, BlockState) -> T,
        vararg blocks: Block
    ): BlockEntityType<T> {
        return BlockEntityType(supplier, Sets.newHashSet(*blocks), null)
    }
}