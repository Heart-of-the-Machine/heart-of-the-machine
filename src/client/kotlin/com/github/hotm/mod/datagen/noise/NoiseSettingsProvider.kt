package com.github.hotm.mod.datagen.noise

import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import com.github.hotm.mod.Log
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import com.mojang.serialization.JsonOps
import net.minecraft.data.DataPackOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier

abstract class NoiseSettingsProvider(output: FabricDataOutput) : DataProvider {
    private val resolver = output.createPathResolver(DataPackOutput.Type.DATA_PACK, "worldgen/noise_settings")
    private val toWrite = mutableListOf<Pair<Path, ChunkGeneratorSettingsDsl>>()

    protected fun noiseSettings(id: Identifier, settings: ChunkGeneratorSettingsDsl) {
        toWrite.add(resolver.resolveJsonFile(id) to settings)
    }

    abstract fun generate()

    override fun run(writer: DataWriter): CompletableFuture<*> {
        generate()

        return CompletableFuture.allOf(*(toWrite.asSequence().map { (path, settings) ->
            val element =
                ChunkGeneratorSettingsDsl.CODEC.encodeStart(JsonOps.INSTANCE, settings)
                    .getOrThrow(false, Log.LOG::error)
            DataProvider.writeAsync(writer, element, path)
        }.toList().toTypedArray()))
    }

    override fun getName(): String = "Noise Settings"
}
