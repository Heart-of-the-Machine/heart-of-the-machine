package com.github.hotm.mod.world.gen.structure

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.structure.piece.StructurePieceType

object HotMStructurePieces {
    val NECTERE_PORTAL: StructurePieceType by lazy {
        StructurePieceType { _, nbt -> NecterePortalStructureFeature.Piece(nbt) }
    }

    fun init() {
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, id("nectere_portal"), NECTERE_PORTAL)
    }
}
