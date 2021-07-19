package com.github.hotm.blockentity

import com.github.hotm.HotMConstants
import com.github.hotm.blocks.HotMBlocks
import com.google.common.collect.Sets
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

object HotMBlockEntities {
    lateinit var NECTERE_PORTAL_SPAWNER: BlockEntityType<NecterePortalSpawnerBlockEntity>
        private set
    lateinit var BASIC_SIPHON_AURA_NODE: BlockEntityType<BasicSiphonAuraNodeBlockEntity>
        private set

    fun register() {
        NECTERE_PORTAL_SPAWNER =
            newBlockEntityType(::NecterePortalSpawnerBlockEntity, HotMBlocks.NECTERE_PORTAL_SPAWNER)
        BASIC_SIPHON_AURA_NODE = newBlockEntityType(::BasicSiphonAuraNodeBlockEntity, HotMBlocks.BASIC_SIPHON_AURA_NODE)

        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner")
        register(BASIC_SIPHON_AURA_NODE, "basic_siphon_aura_node")
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