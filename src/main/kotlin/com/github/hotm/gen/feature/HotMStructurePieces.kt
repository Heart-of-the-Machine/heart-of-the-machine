package com.github.hotm.gen.feature

import net.minecraft.structure.StructurePieceType

/**
 * Manages the Heart of the Machine Structure Pieces.
 */
object HotMStructurePieces {
    val NECTERE_PORTAL =
        StructurePieceType.register({ manager, tag -> NecterePortalStructureFeature.Piece(manager, tag) }, "HotMNePP")

    fun register() {}
}