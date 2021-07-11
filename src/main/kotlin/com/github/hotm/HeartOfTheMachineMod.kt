package com.github.hotm

import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMPortalableBiomes
import com.github.hotm.world.gen.HotMBiomes
import com.github.hotm.world.gen.feature.HotMFeatures

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMBlocks.register()
    HotMItems.register()
    HotMBlockEntities.register()
    HotMFeatures.register()
    HotMBiomes.register()
    HotMDimensions.register()
    HotMCommands.register()
    HotMFuels.register()
    HotMPortalableBiomes.findBiomes()
}
