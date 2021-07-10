package com.github.hotm

import com.github.hotm.world.gen.HotMBiomes
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.gen.feature.HotMFeatures

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMConfig.init()
    HotMBlocks.register()
    HotMItems.register()
    HotMBlockEntities.register()
    HotMFeatures.register()
    HotMBiomes.register()
    HotMDimensions.register()
    HotMCommands.register()
    HotMFuels.register()
    HotMDimensions.findBiomes()
}
