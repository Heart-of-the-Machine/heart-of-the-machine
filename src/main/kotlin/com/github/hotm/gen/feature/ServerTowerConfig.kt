package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.world.gen.feature.FeatureConfig

data class ServerTowerConfig(
    val minSize: Int,
    val maxSize: Int,
    val minHeight: Int,
    val maxHeight: Int,
    val minBorder: Int,
    val maxBorder: Int,
    val maxDrop: Int,
    val flatLampChance: Float,
    val structure: BlockState,
    val lamp: BlockState,
    val flatLamp: BlockState
) : FeatureConfig {
    companion object {
        val CODEC: Codec<ServerTowerConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ServerTowerConfig> ->
                instance.group(
                    Codec.INT.fieldOf("min_size").forGetter { it.minSize },
                    Codec.INT.fieldOf("max_size").forGetter { it.maxSize },
                    Codec.INT.fieldOf("min_height").forGetter { it.minHeight },
                    Codec.INT.fieldOf("max_height").forGetter { it.maxHeight },
                    Codec.INT.fieldOf("min_border").forGetter { it.minBorder },
                    Codec.INT.fieldOf("max_border").forGetter { it.maxBorder },
                    Codec.INT.fieldOf("max_drop").forGetter { it.maxDrop },
                    Codec.FLOAT.fieldOf("flat_lamp_chance").forGetter { it.flatLampChance },
                    BlockState.CODEC.fieldOf("structure").forGetter { it.structure },
                    BlockState.CODEC.fieldOf("lamp").forGetter { it.lamp },
                    BlockState.CODEC.fieldOf("flat_lamp").forGetter { it.flatLamp }
                )
                    .apply(instance) { minSize, maxSize, minHeight, maxHeight, minBorder, maxBorder, maxDrop, flatLampChance, structure, lamp, flatLamp ->
                        ServerTowerConfig(
                            minSize,
                            maxSize,
                            minHeight,
                            maxHeight,
                            minBorder,
                            maxBorder,
                            maxDrop,
                            flatLampChance,
                            structure,
                            lamp,
                            flatLamp
                        )
                    }
            }
    }
}