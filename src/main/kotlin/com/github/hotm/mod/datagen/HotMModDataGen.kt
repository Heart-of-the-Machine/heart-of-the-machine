package com.github.hotm.mod.datagen

import com.github.hotm.mod.HotMLog
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

@Suppress("unused")
object HotMModDataGen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        HotMLog.LOG.info("[HotM] Starting DataGen...")

        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::ModelGen)
        pack.addProvider(::BlockTagGen)
        pack.addProvider(::BlockLootGen)
        pack.addProvider(::RecipeGen)

        pack.addProvider(::NoiseSettingsGen)
        pack.addProvider(::ArchExIntegrationGen)

        HotMLog.LOG.info("[HotM] All data generators registered.")
    }
}
