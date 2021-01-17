package com.github.hotm.world.auranet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos

data class PositionedAuraNode(val pos: BlockPos, val node: AuraNode) {
    companion object {
        val CODEC: Codec<PositionedAuraNode> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PositionedAuraNode> ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(PositionedAuraNode::pos),
                    AuraNode.CODEC.fieldOf("node").forGetter(PositionedAuraNode::node)
                ).apply(instance, ::PositionedAuraNode)
            }
    }
}