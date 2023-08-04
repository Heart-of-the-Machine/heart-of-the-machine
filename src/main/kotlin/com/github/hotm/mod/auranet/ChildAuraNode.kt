package com.github.hotm.mod.auranet

import com.github.hotm.mod.node.aura.AuraLinkEntity
import com.kneelawk.graphlib.api.graph.LinkHolder
import com.kneelawk.graphlib.api.graph.user.LinkKey

interface ChildAuraNode : ConnectableAuraNode {
    fun sumParentLinks(): Float {
        var sum = 0f
        for (link in parentLinks()) {
            val entity = link.getLinkEntity(AuraLinkEntity::class.java)
            if (entity != null) {
                sum += entity.value
            }
        }
        return sum
    }

    fun parentLinks(): Sequence<LinkHolder<LinkKey>> {
        return context.holder.connections.asSequence().filter { link ->
            link.getLinkEntity(AuraLinkEntity::class.java)?.parent?.let { it != context.pos } ?: false
        }
    }

    fun firstParentLink(): LinkHolder<LinkKey>? {
        return context.holder.connections.firstOrNull { link ->
            link.getLinkEntity(AuraLinkEntity::class.java)?.parent?.let { it != context.pos } ?: false
        }
    }
}
