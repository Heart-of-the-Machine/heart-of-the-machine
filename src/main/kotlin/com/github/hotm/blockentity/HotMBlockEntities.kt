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
    lateinit var BASIC_SIPHON_AURA_NODE: BlockEntityType<BasicSiphonAuraNodeBlockEntity>
        private set
    lateinit var COLLECTOR_DISTRIBUTOR_AURA_NODE: BlockEntityType<CollectorDistributorAuraNodeBlockEntity>
        private set
    lateinit var DEBUG_TICKING: BlockEntityType<DebugTickingBlockEntity>
        private set
    lateinit var NECTERE_PORTAL_SPAWNER: BlockEntityType<NecterePortalSpawnerBlockEntity>
        private set

    fun register() {
        BASIC_SIPHON_AURA_NODE = newBlockEntityType(::BasicSiphonAuraNodeBlockEntity, HotMBlocks.BASIC_SIPHON_AURA_NODE)
        COLLECTOR_DISTRIBUTOR_AURA_NODE =
            newBlockEntityType(::CollectorDistributorAuraNodeBlockEntity, HotMBlocks.COLLECTOR_DISTRIBUTOR_AURA_NODE)
        DEBUG_TICKING = newBlockEntityType(::DebugTickingBlockEntity, HotMBlocks.DEBUG_TICKING)
        NECTERE_PORTAL_SPAWNER =
            newBlockEntityType(::NecterePortalSpawnerBlockEntity, HotMBlocks.NECTERE_PORTAL_SPAWNER)

        register(BASIC_SIPHON_AURA_NODE, "basic_siphon_aura_node")
        register(COLLECTOR_DISTRIBUTOR_AURA_NODE, "collector_distributor_aura_node")
        register(DEBUG_TICKING, "debug_ticking")
        register(NECTERE_PORTAL_SPAWNER, "nectere_portal_spawner")
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