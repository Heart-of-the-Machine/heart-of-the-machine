package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConstants.str
import com.github.hotm.net.s2cReadWrite
import com.github.hotm.net.sendToClients
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos

class BasicSiphonAuraNode(access: AuraNetAccess, updateListener: Runnable?, pos: BlockPos, value: Int) :
    AbstractAuraNode(Type, access, updateListener, pos), SiphonAuraNode {

    companion object {
        private val NET_PARENT = AuraNode.NET_ID.subType(
            BasicSiphonAuraNode::class.java,
            str("basic_aura_node")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readVarUnsignedInt() }, { it.writeVarUnsignedInt(value) })
    }

    var value = value
        private set

    fun updateValue(value: Int) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
    }

    override fun recalculateSiphonValue(chunkAura: Int, siphonCount: Int) {
        updateValue(AuraNodeUtils.calculateSiphonValue(10f, 2f, chunkAura, siphonCount))
    }

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeVarUnsignedInt(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as BasicSiphonAuraNode

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }

    object Type : AuraNodeType<BasicSiphonAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<BasicSiphonAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BasicSiphonAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    Codec.INT.fieldOf("value").forGetter(BasicSiphonAuraNode::value)
                ).apply(instance, ::BasicSiphonAuraNode)
            }
        }

        override fun fromPacket(
            access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx
        ): BasicSiphonAuraNode {
            val value = buf.readVarUnsignedInt()
            return BasicSiphonAuraNode(access, null, pos, value)
        }
    }
}