package com.github.hotm

import com.github.hotm.blockentity.HotMBlockEntities
import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.command.HotMCommands
import com.github.hotm.items.HotMItems
import com.github.hotm.misc.HotMBlockTags
import com.github.hotm.misc.HotMFuels
import com.github.hotm.misc.HotMRegistries
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMTeleporters
import com.github.hotm.world.biome.HotMBiomes
import com.github.hotm.world.gen.feature.HotMFeatures

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMRegistries.register()
    HotMBlocks.register()
    HotMBlockTags.register()
    HotMItems.register()
    HotMBlockEntities.register()
    HotMFeatures.register()
    HotMBiomes.register()
    HotMDimensions.register()
    HotMTeleporters.register()
    HotMCommands.register()
    HotMFuels.register()
}
