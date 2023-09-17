package com.github.hotm.mod.auranet

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.Constants.str
import com.github.hotm.mod.util.s2cReadWrite
import com.github.hotm.mod.util.sendToClients
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.graphlib.api.graph.GraphWorld
import com.kneelawk.graphlib.api.graph.user.NodeEntity
import com.kneelawk.graphlib.api.graph.user.NodeEntityType

class SimpleSiphonAuraNode(value: Float) : AbstractSiphonAuraNode(), ParentAuraNode, SiphonAuraNode,
    RecalculableAuraNode, ValuedAuraNode {
    companion object {
        val TYPE = NodeEntityType.of(id("simple_siphon_aura_node"), {
            val tag = it as? NbtCompound ?: return@of null
            SimpleSiphonAuraNode(tag.getFloat("value"))
        }, { buf, _ ->
            SimpleSiphonAuraNode(buf.readFloat())
        })

        private val NET_PARENT = NodeEntity.NET_PARENT.subType(
            SimpleSiphonAuraNode::class.java,
            str("simple_siphon_aura_node")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readFloat() }, { it.writeFloat(value) })
    }

    override var value = value
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
        val data = getSiphonData()
        updateValue(AuraNodeUtils.calculateSiphonValue(10f, 2f, data.currentAura, data.siphonCount))

        // apply child link value
        setFirstChildLink(value)
    }

    override fun preAddChild(child: ChildAuraNode) {
        val graphWorld = context.graphWorld as? GraphWorld ?: return
        for (link in context.holder.connections.toList()) {
            val oldChild = link.other(context.pos).getNodeEntity(ChildAuraNode::class.java) ?: continue
            AuraNodeUtils.disconnect(graphWorld, this, oldChild)
        }
    }
}