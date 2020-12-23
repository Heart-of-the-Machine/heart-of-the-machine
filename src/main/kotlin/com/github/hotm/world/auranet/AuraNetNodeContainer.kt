package com.github.hotm.world.auranet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos

data class AuraNetNodeContainer(val pos: BlockPos, val node: AuraNetNode) {
    companion object {
        fun createCodec(updateListener: Runnable): Codec<AuraNetNodeContainer> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<AuraNetNodeContainer> ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(AuraNetNodeContainer::pos),
                    AuraNetNode.createCodec(updateListener).fieldOf("node").forGetter(AuraNetNodeContainer::node)
                ).apply(instance, ::AuraNetNodeContainer)
            }
        }
    }
}