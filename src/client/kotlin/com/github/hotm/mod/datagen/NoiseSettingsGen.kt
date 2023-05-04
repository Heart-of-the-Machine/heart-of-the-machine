package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.Log
import com.github.hotm.mod.block.HotMBlocks
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.Blocks
import net.minecraft.data.DataPackOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.VerticalSurfaceType
import net.minecraft.world.gen.DensityFunctions.*
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.minecraft.world.gen.chunk.GenerationShapeConfig
import net.minecraft.world.gen.noise.NoiseRouter
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.*
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

class NoiseSettingsGen(output: FabricDataOutput, private val lookupProvider: CompletableFuture<HolderLookup.Provider>) :
    DataProvider {
    private val resolver = output.createPathResolver(DataPackOutput.Type.DATA_PACK, "noise_settings")
    private val toWrite = mutableListOf<Pair<Path, ChunkGeneratorSettings>>()

    override fun run(writer: DataWriter): CompletableFuture<*> {
        return lookupProvider.thenAccept { provider ->
            generate(provider)
        }.thenCompose {
            CompletableFuture.allOf(*(toWrite.asSequence().map { (path, settings) ->
                val element =
                    ChunkGeneratorSettings.CODEC.encodeStart(JsonOps.INSTANCE, settings)
                        .getOrThrow(false, Log.LOG::error)
                DataProvider.writeAsync(writer, element, path)
            }.toList().toTypedArray()))
        }
    }

    private fun generate(provider: HolderLookup.Provider) {
        val densityFunction = provider.getLookup(RegistryKeys.DENSITY_FUNCTION).get()
        val shiftX =
            densityFunction.getHolderOrThrow(RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, Identifier("shift_x")))
                .value()
        val shiftZ =
            densityFunction.getHolderOrThrow(RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, Identifier("shift_z")))
                .value()
        val nectereCave3dNoise =
            densityFunction.getHolderOrThrow(RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, id("nectere/cave_3d_noise")))
                .value()
        val nectereSurface3dNoise = densityFunction.getHolderOrThrow(
            RegistryKey.of(
                RegistryKeys.DENSITY_FUNCTION,
                id("nectere/surface_3d_noise")
            )
        ).value()

        val noise = provider.getLookup(RegistryKeys.NOISE_PARAMETERS).get()
        val temperature =
            noise.getHolderOrThrow(RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, Identifier("temperature")))
        val vegetation = noise.getHolderOrThrow(RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, Identifier("vegetation")))

        noiseSettings(
            id("nectere"), ChunkGeneratorSettings(
                GenerationShapeConfig(-64, 448, 1, 2),
                HotMBlocks.THINKING_STONE.defaultState,
                Blocks.WATER.defaultState,
                NoiseRouter(
                    zero(),
                    zero(),
                    zero(),
                    zero(),
                    shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        temperature
                    ),
                    shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        vegetation
                    ),
                    zero(),
                    zero(),
                    zero(),
                    zero(),
                    zero(),
                    multiply(
                        constant(0.64),
                        interpolated(
                            blendDensity(
                                add(
                                    multiply(
                                        clampedGradientY(160, 224, 1.0, 0.0),
                                        add(
                                            constant(-0.15),
                                            add(
                                                constant(2.5),
                                                multiply(
                                                    clampedGradientY(-72, -40, 0.0, 1.0),
                                                    add(
                                                        constant(-2.5),
                                                        add(
                                                            constant(0.9375),
                                                            multiply(
                                                                clampedGradientY(160, 224, 1.0, 0.85),
                                                                add(
                                                                    constant(-0.9375),
                                                                    nectereCave3dNoise
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                    multiply(
                                        clampedGradientY(160, 224, 0.0, 1.0),
                                        add(
                                            constant(2.5),
                                            multiply(
                                                clampedGradientY(160, 224, 0.65, 1.0),
                                                add(
                                                    constant(-2.5),
                                                    add(
                                                        constant(-0.9375),
                                                        multiply(
                                                            clampedGradientY(192, 256, 1.0, 0.0),
                                                            add(
                                                                constant(0.9375),
                                                                nectereSurface3dNoise
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).squeeze(),
                    zero(),
                    zero(),
                    zero()
                ),
                sequence(
                    condition(
                        verticalGradient("minecraft:bedrock_floor", YOffset.aboveBottom(0), YOffset.aboveBottom(5)),
                        block(Blocks.BEDROCK.defaultState)
                    ),
                    condition(
                        biome(
                            RegistryKey.of(RegistryKeys.BIOME, id("thinking_forest")),
                            RegistryKey.of(RegistryKeys.BIOME, id("meditating_fields"))
                        ),
                        condition(
                            water(1, 0),
                            sequence(
                                condition(
                                    stoneDepth(0, false, VerticalSurfaceType.FLOOR),
                                    block(HotMBlocks.PLASSEIN_THINKING_SCRAP.defaultState)
                                ),
                                condition(
                                    stoneDepth(1, false, VerticalSurfaceType.FLOOR),
                                    block(HotMBlocks.THINKING_SCRAP.defaultState)
                                ),
                                condition(
                                    stoneDepth(2, false, VerticalSurfaceType.FLOOR),
                                    block(HotMBlocks.THINKING_SCRAP.defaultState)
                                ),
                                condition(
                                    stoneDepth(3, false, VerticalSurfaceType.FLOOR),
                                    block(HotMBlocks.THINKING_SCRAP.defaultState)
                                )
                            )
                        )
                    )
                ),
                listOf(),
                0,
                false,
                false,
                false,
                true
            )
        )
    }

    private fun noiseSettings(id: Identifier, settings: ChunkGeneratorSettings) {
        toWrite.add(resolver.resolveJsonFile(id) to settings)
    }

    override fun getName(): String = "Noise Settings"
}
