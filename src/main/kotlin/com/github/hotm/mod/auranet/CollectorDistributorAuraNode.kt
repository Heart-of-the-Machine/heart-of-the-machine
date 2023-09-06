package com.github.hotm.mod.auranet

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.Constants.str
import com.github.hotm.mod.util.s2cReadWrite
import com.github.hotm.mod.util.sendToClients
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.graphlib.api.graph.user.NodeEntity
import com.kneelawk.graphlib.api.graph.user.NodeEntityType

class CollectorDistributorAuraNode(value: Float) : AbstractAuraNode(), ParentAuraNode, ChildAuraNode,
    RecalculableAuraNode, ValuedAuraNode {
    companion object {
        val TYPE = NodeEntityType.of(id("collector_distributor_aura_node"), {
            val tag = it as? NbtCompound ?: return@of null
            CollectorDistributorAuraNode(tag.getFloat("value"))
        }, { buf, _ -> CollectorDistributorAuraNode(buf.readFloat()) })

        private val NET_PARENT = NodeEntity.NET_PARENT.subType(
            CollectorDistributorAuraNode::class.java, str("collector_distributor_aura_node")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readFloat() }, { it.writeFloat(value) })
    }

    override var value: Float = value
        private set

    fun updateValue(value: Float) {
        this.value = value
        context.markDirty()
        ID_VALUE_CHANGE.sendToClients(context, this)
    }

    override fun getType(): NodeEntityType = TYPE

    override fun toTag(): NbtElement {
        val tag = NbtCompound()
        tag.putFloat("value", value)
        return tag
    }

    override fun toPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeFloat(value)
    }

    override fun recalculateValue(getSiphonData: () -> SiphonChunkData) {
        updateValue(sumParentLinks())
        distributeAmongChildLinks(value)
    }
}
