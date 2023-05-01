package com.github.hotm.mod.datagen

import com.github.hotm.mod.Log
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object HotMModDataGen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        Log.LOG.info("[HotM] Starting DataGen...")

        Log.LOG.info("[HotM] DataGen complete.")
    }
}
