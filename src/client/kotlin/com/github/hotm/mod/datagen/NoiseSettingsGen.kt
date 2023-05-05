package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.datagen.noise.NoiseSettingsProvider
import com.github.hotm.mod.datagen.noise.df
import com.github.hotm.mod.datagen.noise.shiftedNoise
import com.github.hotm.mod.datagen.noise.yGradient
import com.github.hotm.mod.util.asBiomeKey
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier
import net.minecraft.util.math.VerticalSurfaceType
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.*

class NoiseSettingsGen(output: FabricDataOutput) : NoiseSettingsProvider(output) {
    override fun generate() {
        noiseSettings(id("nectere")) {
            seaLevel(0)
            disableMobGeneration(false)
            aquifersEnabled(false)
            oreVeinsEnabled(false)
            legacyRandomSource(false)

            defaultBlock(HotMBlocks.THINKING_STONE.defaultState)

            noise(-64, 448, 1, 2)

            noiseRouter {
                temperature = shiftedNoise(
                    noise = Identifier("minecraft:temperature"),
                    xzScale = 0.25,
                    shiftX = "minecraft:shift_x".df,
                    shiftZ = "minecraft:shift_z".df
                )
                vegetation = shiftedNoise(
                    noise = Identifier("minecraft:vegetation"),
                    xzScale = 0.25,
                    shiftX = "minecraft:shift_x".df,
                    shiftZ = "minecraft:shift_z".df
                )

                val upperBoundedCaves =
                    0.9375.df + yGradient(160, 224, 1.0, 0.85) * (id("nectere/cave_3d_noise").df - 0.9375.df)
                val caves = 2.5.df - 0.15.df + yGradient(-72, -40, 0.0, 1.0) * (upperBoundedCaves - 2.5.df)
                val upperBoundedSurface =
                    yGradient(192, 256, 1.0, 0.0) * (0.9375.df + id("nectere/surface_3d_noise").df) - 0.9375.df
                val surface = 2.5.df + yGradient(160, 224, 0.65, 1.0) * (upperBoundedSurface - 2.5.df)

                finalDensity =
                    ((yGradient(160, 224, 1.0, 0.0) * caves + yGradient(160, 224, 0.0, 1.0) * surface).blendDensity()
                        .interpolated() * 0.64.df).squeeze()
            }

            surfaceRule(
                sequence(
                    condition(
                        verticalGradient("minecraft:bedrock_floor", YOffset.aboveBottom(0), YOffset.aboveBottom(5)),
                        block(Blocks.BEDROCK.defaultState)
                    ),
                    condition(
                        biome(
                            id("thinking_forest").asBiomeKey(),
                            id("meditating_fields").asBiomeKey()
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
        }
    }
}
