package com.github.hotm.mod.datagen

import com.github.hotm.mod.Log
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

@Suppress("unused")
object HotMModDataGen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        Log.LOG.info("[HotM] Starting DataGen...")

        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::BlockModelGen)
        pack.addProvider(::BlockTagGen)
        pack.addProvider(::BlockLootGen)

        Log.LOG.info("[HotM] DataGen complete.")
    }
}
