package com.github.hotm

import com.github.hotm.world.gen.HotMBiomes
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.auranet.AuraNodes
import com.github.hotm.world.gen.feature.HotMFeatures

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMConfig.init()
    HotMRegistries.register()
    HotMBlocks.register()
    HotMItems.register()
    HotMBlockEntities.register()
    HotMBiomes.register()
    HotMDimensions.register()
    AuraNodes.register()
    HotMFeatures.register()
    HotMCommands.register()
    HotMFuels.register()
    HotMDimensions.findBiomes()
}
