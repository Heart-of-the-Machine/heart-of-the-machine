package com.github.hotm.world.auranet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos

data class AuraNodeContainer(val pos: BlockPos, val node: AuraNode) {
    companion object {
        val CODEC: Codec<AuraNodeContainer> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNodeContainer> ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(AuraNodeContainer::pos),
                    AuraNode.CODEC.fieldOf("node").forGetter(AuraNodeContainer::node)
                ).apply(instance, ::AuraNodeContainer)
            }
    }
}