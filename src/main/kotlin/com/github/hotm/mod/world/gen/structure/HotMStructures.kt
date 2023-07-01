package com.github.hotm.mod.world.gen.structure

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.structure.StructureType

object HotMStructures {
    val NECTERE_PORTAL_TYPE: StructureType<NecterePortalStructureFeature> by lazy { StructureType { NecterePortalStructureFeature.CODEC } }

    fun init() {
        Registry.register(Registries.STRUCTURE_TYPE, id("nectere_portal"), NECTERE_PORTAL_TYPE)
    }
}
