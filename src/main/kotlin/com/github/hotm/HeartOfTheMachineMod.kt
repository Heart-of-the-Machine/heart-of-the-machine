package com.github.hotm

import com.github.hotm.gen.HotMBiomes
import com.github.hotm.gen.HotMDimensions
import com.github.hotm.gen.feature.HotMBiomeFeatures
import com.github.hotm.gen.feature.HotMStructureFeatures
import com.github.hotm.gen.feature.HotMStructurePieces

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMBlocks.register()
    HotMBiomes.register()
    HotMDimensions.register()
    HotMBiomeFeatures.register()
}
