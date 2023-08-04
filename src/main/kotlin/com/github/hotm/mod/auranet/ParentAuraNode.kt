package com.github.hotm.mod.auranet

import com.github.hotm.mod.node.aura.AuraLinkEntity
import com.github.hotm.mod.util.MaybeNode
import com.kneelawk.graphlib.api.graph.LinkHolder
import com.kneelawk.graphlib.api.graph.user.LinkKey

interface ParentAuraNode : ConnectableAuraNode {
    fun getChildNodes(): Sequence<MaybeNode> {
        return childLinks().map { MaybeNode.of(it.other(context.pos)) }
    }

    fun distributeAmongChildLinks(value: Float) {
        val childLinks = childLinks().toList()
        val distributed = value / childLinks.size

        for (link in childLinks) {
            link.getLinkEntity(AuraLinkEntity::class.java)?.updateValue(distributed)
        }
    }

    fun setFirstChildLink(value: Float) {
        firstChildLink()?.getLinkEntity(AuraLinkEntity::class.java)?.updateValue(value)
    }

    fun childLinks(): Sequence<LinkHolder<LinkKey>> {
        return context.holder.connections.asSequence()
            .filter { it.getLinkEntity(AuraLinkEntity::class.java)?.parent == context.pos }
    }

    fun firstChildLink(): LinkHolder<LinkKey>? {
        return context.holder.connections.firstOrNull { it.getLinkEntity(AuraLinkEntity::class.java)?.parent == context.pos }
    }
}
