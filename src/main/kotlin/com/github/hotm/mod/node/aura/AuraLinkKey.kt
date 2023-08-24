package com.github.hotm.mod.node.aura

import java.util.function.Supplier
import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.auranet.ParentAuraNode
import net.minecraft.nbt.NbtElement
import com.kneelawk.graphlib.api.graph.LinkHolder
import com.kneelawk.graphlib.api.graph.user.LinkEntity
import com.kneelawk.graphlib.api.graph.user.LinkKey
import com.kneelawk.graphlib.api.graph.user.LinkKeyType

object AuraLinkKey : LinkKey {
    val TYPE = LinkKeyType.of(id("aura"), Supplier { AuraLinkKey })

    override fun getType(): LinkKeyType = TYPE

    override fun toTag(): NbtElement? = null

    override fun isAutomaticRemoval(holder: LinkHolder<LinkKey>): Boolean = false

    override fun shouldHaveLinkEntity(holder: LinkHolder<LinkKey>): Boolean = true

    override fun createLinkEntity(holder: LinkHolder<LinkKey>): LinkEntity {
        HotMLog.LOG.error("Automatically creating missing node entity at ${holder.pos}. This should never need to happen.")

        if (holder.first.getNodeEntity(ParentAuraNode::class.java) != null)
            return AuraLinkEntity(holder.first.pos, 0f)

        return AuraLinkEntity(holder.second.pos, 0f)
    }
}
