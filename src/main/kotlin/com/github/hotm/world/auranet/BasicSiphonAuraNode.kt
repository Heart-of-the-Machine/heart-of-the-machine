package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConstants.str
import com.github.hotm.net.s2cReadWrite
import com.github.hotm.net.sendToClients
import com.github.hotm.util.DimBlockPos
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos
import java.util.*

class BasicSiphonAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    value: Int,
    childPos: BlockPos?
) :
    AbstractAuraNode(Type, access, updateListener, pos), SiphonAuraNode, DependableAuraNode {

    companion object {
        private val NET_PARENT = AuraNode.NET_ID.subType(
            BasicSiphonAuraNode::class.java,
            str("basic_aura_node")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readVarUnsignedInt() }, { it.writeVarUnsignedInt(value) })
        private val ID_CHILD_POS_CHANGE = NET_PARENT.idData("CHILD_POS_CHANGE")
            .s2cReadWrite(
                { childPos = if (it.readBoolean()) it.readBlockPos() else null },
                {
                    val childPos = childPos
                    it.writeBoolean(childPos != null)
                    childPos?.let { pos -> it.writeBlockPos(pos) }
                })
    }

    var value = value
        private set

    var childPos = childPos
        private set

    fun updateValue(value: Int) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
    }

    fun updateChildPos(childPos: BlockPos?) {
        this.childPos = childPos
        markDirty()
        ID_CHILD_POS_CHANGE.sendToClients(world, pos, this)
    }

    override fun recalculateSiphonValue(chunkAura: Int, siphonCount: Int) {
        updateValue(AuraNodeUtils.calculateSiphonValue(10f, 2f, chunkAura, siphonCount))
    }

    override fun isChildValid(node: DependantAuraNode): Boolean {
        // TODO: evaluate whether this should be done here
        return !node.wouldCauseDepencencyLoop(this)
    }

    override fun addChild(node: DependantAuraNode) {
        // remove previous child
        // TODO: evaluate whether to do this here or in connectChild(...)
        childPos?.let { childPos ->
            (access[childPos] as? DependantAuraNode)?.removeParent(dimPos)
        }
        updateChildPos(node.pos)
    }

    override fun removeChild(pos: DimBlockPos) {
        if (pos.dim != world.registryKey) return
        if (pos.pos != childPos) return

        updateChildPos(null)
    }

    override fun connectChild(pos: DimBlockPos) {
        // TODO: should this be some kind of assertion?
        //  Should I even be using DimBlockPos for general dependency tracking?
        if (pos.dim != world.registryKey) return

        // TODO: figure out what variants I need to uphold here. Do I need to add myself to a child's list of parents
        //  before or after I've added that child to my list of children?
        childPos?.let { childPos ->
            (access[childPos] as? DependantAuraNode)?.removeParent(dimPos)
        }
        (access[pos.pos] as? DependantAuraNode)?.let { child ->
            child.addParent(this)
            updateChildPos(pos.pos)
        }
    }

    override fun disconnectChild(pos: DimBlockPos) {
        if (pos.dim != world.registryKey) return
        if (pos.pos != childPos) return

        childPos?.let { childPos ->
            (access[childPos] as? DependantAuraNode)?.removeParent(dimPos)
        }
        updateChildPos(null)
    }

    override fun getSuppliedAura(child: DependantAuraNode): Int {
        return value
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
                    Codec.INT.fieldOf("value").forGetter(BasicSiphonAuraNode::value),
                    BlockPos.CODEC.optionalFieldOf("child_pos").xmap({ it.orElse(null) }, { Optional.ofNullable(it) })
                        .forGetter(BasicSiphonAuraNode::childPos)
                ).apply(instance, ::BasicSiphonAuraNode)
            }
        }

        override fun fromPacket(
            access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx
        ): BasicSiphonAuraNode {
            val value = buf.readVarUnsignedInt()
            val childPos = if (buf.readBoolean()) buf.readBlockPos() else null
            return BasicSiphonAuraNode(access, null, pos, value, childPos)
        }
    }
}