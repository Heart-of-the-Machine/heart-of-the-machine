package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.world.gen.feature.FeatureConfig

data class TransmissionTowerConfig(
    val minHeight: Int,
    val maxHeight: Int,
    val minFalloff: Int,
    val maxFalloff: Int,
    val maxDrop: Int,
    val growthChance: Float,
    val leafChance: Float,
    val structure: BlockState,
    val structureGrowth: BlockState,
    val leaf: BlockState,
    val lamp: BlockState
) : FeatureConfig {
    companion object {
        val CODEC: Codec<TransmissionTowerConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<TransmissionTowerConfig> ->
                instance.group(
                    Codec.INT.fieldOf("min_height").forGetter { it.minHeight },
                    Codec.INT.fieldOf("max_height").forGetter { it.maxHeight },
                    Codec.INT.fieldOf("min_falloff").forGetter { it.minFalloff },
                    Codec.INT.fieldOf("max_falloff").forGetter { it.maxFalloff },
                    Codec.INT.fieldOf("max_drop").forGetter { it.maxDrop },
                    Codec.FLOAT.fieldOf("growth_chance").forGetter { it.growthChance },
                    Codec.FLOAT.fieldOf("leaf_chance").forGetter { it.leafChance },
                    BlockState.CODEC.fieldOf("structure").forGetter { it.structure },
                    BlockState.CODEC.fieldOf("structure_growth").forGetter { it.structureGrowth },
                    BlockState.CODEC.fieldOf("leaf").forGetter { it.leaf },
                    BlockState.CODEC.fieldOf("lamp").forGetter { it.lamp })
                    .apply(instance) { minHeight, maxHeight, minFalloff, maxFalloff, maxDrop, growthChance, leafChance, structure, structureGrowth, leaf, lamp ->
                        TransmissionTowerConfig(
                            minHeight,
                            maxHeight,
                            minFalloff,
                            maxFalloff,
                            maxDrop,
                            growthChance,
                            leafChance,
                            structure,
                            structureGrowth,
                            leaf,
                            lamp
                        )
                    }
            }
    }
}