package com.github.hotm.gen.feature

import net.minecraft.structure.StructurePieceType

/**
 * Manages the Heart of the Machine Structure Pieces.
 */
object HotMStructurePieces {
    val NECTERE_PORTAL = StructurePieceType { manager, tag -> NecterePortalStructureFeature.Piece(manager, tag) }

    fun register() {
        StructurePieceType.register(NECTERE_PORTAL, "HotMNePP")
    }
}