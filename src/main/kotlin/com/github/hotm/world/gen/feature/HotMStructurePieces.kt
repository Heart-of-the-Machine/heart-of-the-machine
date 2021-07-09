package com.github.hotm.world.gen.feature

import net.minecraft.structure.StructurePieceType

/**
 * Manages the Heart of the Machine Structure Pieces.
 */
object HotMStructurePieces {
    // NOTE: The id should have been: "hotm:NePP"
    val NECTERE_PORTAL =
        StructurePieceType.register(NecterePortalStructureFeature::Piece, "hotm:NePP")

    fun register() {}
}