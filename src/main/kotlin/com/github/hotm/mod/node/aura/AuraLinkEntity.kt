package com.github.hotm.mod.node.aura

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.Constants.str
import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.util.s2cReadWrite
import com.github.hotm.mod.util.sendToClients
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.graphlib.api.graph.user.AbstractLinkEntity
import com.kneelawk.graphlib.api.graph.user.LinkEntity
import com.kneelawk.graphlib.api.graph.user.LinkEntityType
import com.kneelawk.graphlib.api.util.NodePos

class AuraLinkEntity(val parent: NodePos, value: Float) : AbstractLinkEntity() {
    companion object {
        val TYPE = LinkEntityType.of(id("aura"), {
            val tag = it as? NbtCompound ?: return@of null
            val parent = NodePos.fromNbt(tag.getCompound("parent"), HotMUniverses.AURA) ?: return@of null
            AuraLinkEntity(parent, tag.getFloat("value"))
        }, { buf, ctx ->
            val parent = NodePos.fromPacket(buf, ctx, HotMUniverses.AURA)
            val value = buf.readFloat()
            AuraLinkEntity(parent, value)
        })

        private val NET_PARENT = LinkEntity.NET_PARENT.subType(
            AuraLinkEntity::class.java,
            str("aura_link_entity")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readFloat() }, { it.writeFloat(value) })
    }

    var value = value
        private set

    fun updateValue(value: Float) {
        this.value = value
        context.markDirty()
        ID_VALUE_CHANGE.sendToClients(context, this)
    }

    override fun getType(): LinkEntityType = TYPE

    override fun toTag(): NbtElement {
        val tag = NbtCompound()
        tag.put("parent", parent.toNbt())
        tag.putFloat("value", value)
        return tag
    }

    override fun toPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        super.toPacket(buf, ctx)
        parent.toPacket(buf, ctx)
        buf.writeFloat(value)
    }
}
