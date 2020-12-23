package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

data class BasicAuraNode(val value: Float) : AuraNetNode {
    companion object {
        val CODEC: Codec<BasicAuraNode> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BasicAuraNode> ->
            instance.group(Codec.FLOAT.fieldOf("value").forGetter(BasicAuraNode::value))
                .apply(instance, ::BasicAuraNode)
        }
    }

    override val codec = CODEC

    override fun storageEquals(auraNetNode: AuraNetNode): Boolean {
        return this == auraNetNode
    }

    override fun recalculate(world: ServerWorld, pos: BlockPos): AuraNetNode {
        TODO("Not yet implemented")
    }

    override fun dependenants(world: ServerWorld, pos: BlockPos): Collection<DimBlockPos> {
        TODO("Not yet implemented")
    }
}