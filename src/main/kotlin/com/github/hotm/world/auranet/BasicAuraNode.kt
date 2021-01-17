package com.github.hotm.world.auranet

import com.github.hotm.HotMBlocks
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class BasicAuraNode(val value: Int) : AuraNode {
    companion object {
        val CODEC: Codec<BasicAuraNode> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BasicAuraNode> ->
                instance.group(Codec.INT.fieldOf("value").forGetter(BasicAuraNode::value))
                    .apply(instance, ::BasicAuraNode)
            }
    }

    override val codec = CODEC
    override val block = HotMBlocks.BASIC_AURA_NODE

    override fun storageEquals(auraNode: AuraNode): Boolean {
        return this == auraNode
    }
}