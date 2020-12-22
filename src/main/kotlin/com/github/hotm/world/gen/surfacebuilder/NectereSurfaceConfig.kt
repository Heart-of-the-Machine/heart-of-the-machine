package com.github.hotm.world.gen.surfacebuilder

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig

data class NectereSurfaceConfig(
    private val topMaterial: BlockState,
    private val underMaterial: BlockState,
    val beachMaterial: BlockState
) : SurfaceConfig {
    companion object {
        val CODEC: Codec<NectereSurfaceConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<NectereSurfaceConfig> ->
                instance.group(
                    BlockState.CODEC.fieldOf("top_material").forGetter(NectereSurfaceConfig::topMaterial),
                    BlockState.CODEC.fieldOf("under_material").forGetter(NectereSurfaceConfig::underMaterial),
                    BlockState.CODEC.fieldOf("beach_material").forGetter(NectereSurfaceConfig::beachMaterial)
                ).apply(instance) { topMaterial, underMaterial, beachMaterial ->
                    NectereSurfaceConfig(topMaterial, underMaterial, beachMaterial)
                }
            }
    }

    override fun getTopMaterial(): BlockState {
        return topMaterial
    }

    override fun getUnderMaterial(): BlockState {
        return underMaterial
    }
}