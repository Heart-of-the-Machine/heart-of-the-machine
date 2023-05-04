package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.datagen.noise.*
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.VerticalSurfaceType
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.chunk.GenerationShapeConfig
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.*

class NoiseSettingsGen(output: FabricDataOutput) : NoiseSettingsProvider(output) {
    override fun generate() {
        val upperBoundedCaves =
            0.9375.df + yGradient(160, 224, 1.0, 0.85) * (id("nectere/cave_3d_noise").df - 0.9375.df)
        val caves = 2.5.df - 0.15.df + yGradient(-72, -40, 0.0, 1.0) * (upperBoundedCaves - 2.5.df)
        val upperBoundedSurface =
            yGradient(192, 256, 1.0, 0.0) * (0.9375.df + id("nectere/surface_3d_noise").df) - 0.9375.df
        val surface = 2.5.df + yGradient(160, 224, 0.65, 1.0) * (upperBoundedSurface - 2.5.df)

        val finalDensity =
            ((yGradient(160, 224, 1.0, 0.0) * caves + yGradient(160, 224, 0.0, 1.0) * surface).blendDensity()
                .interpolated() * 0.64.df).squeeze()

        noiseSettings(
            id("nectere"),
            chunkGeneratorSettings(
                seaLevel = 0,
                mobGenerationDisabled = false,
                aquifersEnabled = false,
                oreVeinsEnabled = false,
                useLegacyRandomGenerator = true,
                defaultBlock = HotMBlocks.THINKING_STONE.defaultState,
                generationShapeConfig = GenerationShapeConfig(-64, 448, 1, 2),
                noiseRouter = noiseRouter(
                    temperature = shiftedNoise(
                        noise = Identifier("minecraft:temperature"),
                        xzScale = 0.25,
                        shiftX = "minecraft:shift_x".df,
                        shiftZ = "minecraft:shift_z".df
                    ),
                    vegetation = shiftedNoise(
                        noise = Identifier("minecraft:vegetation"),
                        xzScale = 0.25,
                        shiftX = "minecraft:shift_x".df,
                        shiftZ = "minecraft:shift_z".df
                    ),
                    fullNoise = finalDensity
                ),
                surfaceRule = sequence(
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
                )
            )
        )
    }
}
