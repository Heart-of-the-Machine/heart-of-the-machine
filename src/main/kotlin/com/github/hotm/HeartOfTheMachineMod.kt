package com.github.hotm

import com.github.hotm.gen.HotMBiomes
import com.github.hotm.gen.HotMDimensions
import com.github.hotm.gen.feature.HotMFeatures

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMConfig.init()
    HotMBlocks.register()
    HotMItems.register()
    HotMBlockEntities.register()
    HotMBiomes.register()
    HotMDimensions.register()
    HotMFeatures.register()
    HotMCommands.register()
    HotMDimensions.findBiomes()
}
