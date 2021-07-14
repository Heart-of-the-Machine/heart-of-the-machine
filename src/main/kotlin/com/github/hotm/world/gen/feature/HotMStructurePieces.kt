package com.github.hotm.world.gen.feature

import net.minecraft.structure.StructurePieceType

/**
 * Manages the Heart of the Machine Structure Pieces.
 */
object HotMStructurePieces {
    lateinit var NECTERE_PORTAL: StructurePieceType
        private set

    fun register() {
        NECTERE_PORTAL = StructurePieceType.register(NecterePortalStructureFeature::Piece, "hotm:NePP")
    }
}