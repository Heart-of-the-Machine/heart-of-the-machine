package com.github.hotm.world.gen.feature

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.FeatureConfig

data class CrystalGrowthConfig(
    val targets: List<Block>,
    val crystal: BlockState,
    val size: Int,
    val splitChance: Float
) : FeatureConfig {
    companion object {
        val CODEC: Codec<CrystalGrowthConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CrystalGrowthConfig> ->
                instance.group(
                    Registry.BLOCK.listOf().fieldOf("targets").forGetter { it.targets },
                    BlockState.CODEC.fieldOf("crystal").forGetter { it.crystal },
                    Codec.INT.fieldOf("size").forGetter { it.size },
                    Codec.FLOAT.fieldOf("split_chance").forGetter { it.splitChance }
                ).apply(instance) { targets, crystal, size, splitChance ->
                    CrystalGrowthConfig(
                        targets,
                        crystal,
                        size,
                        splitChance
                    )
                }
            }
    }
}