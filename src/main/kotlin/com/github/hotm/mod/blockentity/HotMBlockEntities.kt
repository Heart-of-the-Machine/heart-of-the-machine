package com.github.hotm.mod.blockentity

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object HotMBlockEntities {
    val NECTERE_PORTAL_SPAWNER = BlockEntityType(::NecterePortalSpawnerBlockEntity, setOf(HotMBlocks.NECTERE_PORTAL_SPAWNER), null)

    fun init() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("nectere_portal_spawner"), NECTERE_PORTAL_SPAWNER)
    }
}
