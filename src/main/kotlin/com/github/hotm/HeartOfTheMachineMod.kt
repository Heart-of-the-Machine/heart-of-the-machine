package com.github.hotm

import com.github.hotm.gen.HotMBiomes
import com.github.hotm.gen.HotMDimensions

/**
 * Initializer for Heart of the Machine mod.
 */
@Suppress("unused")
fun init() {
    HotMBlocks.register()
    HotMBiomes.register()
    HotMDimensions.register()
}
