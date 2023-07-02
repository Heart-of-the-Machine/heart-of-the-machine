package com.github.hotm.mod

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.block.HotMPointOfInterestTypes
import com.github.hotm.mod.blockentity.HotMBlockEntities
import com.github.hotm.mod.misc.HotMCreativeTabs
import com.github.hotm.mod.world.biome.NecterePortalData
import com.github.hotm.mod.world.gen.carver.HotMCarvers
import com.github.hotm.mod.world.gen.feature.HotMFeatures
import com.github.hotm.mod.world.gen.structure.HotMStructurePieces
import com.github.hotm.mod.world.gen.structure.HotMStructures
import com.github.hotm.mod.world.gen.surfacebuilder.HotMSurfaceBuilders
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("unused")
object HotMMod : ModInitializer {
    override fun onInitialize(mod: ModContainer) {
        HotMLog.LOG.info("[HotM] Initializing Heart of the Machine v${Constants.MOD_VERSION}...")

        NecterePortalData.init()

        HotMBlocks.init()
        HotMBlockEntities.init()
        HotMPointOfInterestTypes.init()
        HotMCarvers.init()
        HotMFeatures.init()
        HotMSurfaceBuilders.init()
        HotMStructures.init()
        HotMStructurePieces.init()

        HotMCreativeTabs.init()

        HotMLog.LOG.info("[HotM] Heart of the Machine initialized.")
    }
}
