package com.github.hotm.mod.datagen.noise

import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import com.github.hotm.mod.Log
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import com.mojang.serialization.JsonOps
import net.minecraft.data.DataPackOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.RegistryOps
import net.minecraft.util.Identifier

abstract class NoiseSettingsProvider(
    output: FabricDataOutput, private val providerFuture: CompletableFuture<HolderLookup.Provider>
) : DataProvider {
    private val resolver = output.createPathResolver(DataPackOutput.Type.DATA_PACK, "worldgen/noise_settings")
    private val toWrite = mutableListOf<Pair<Path, ChunkGeneratorSettingsDsl>>()

    protected fun noiseSettings(id: Identifier, settings: ChunkGeneratorSettingsDsl) {
        toWrite.add(resolver.resolveJsonFile(id) to settings)
    }

    protected fun noiseSettings(id: Identifier, configure: ChunkGeneratorSettingsDsl.Builder.() -> Unit) {
        val builder = ChunkGeneratorSettingsDsl.builder()
        builder.configure()
        toWrite.add(resolver.resolveJsonFile(id) to builder.build())
    }

    abstract fun generate()

    override fun run(writer: DataWriter): CompletableFuture<*> {
        return providerFuture.thenApply { provider ->
            generate()
            provider
        }.thenCompose { provider ->
            CompletableFuture.allOf(*(toWrite.asSequence().map { (path, settings) ->
                val ops = RegistryOps.create(JsonOps.INSTANCE, provider)

                val element =
                    ChunkGeneratorSettingsDsl.CODEC.encodeStart(ops, settings)
                        .getOrThrow(false, Log.LOG::error)
                DataProvider.writeAsync(writer, element, path)
            }.toList().toTypedArray()))
        }
    }

    override fun getName(): String = "Noise Settings"
}
