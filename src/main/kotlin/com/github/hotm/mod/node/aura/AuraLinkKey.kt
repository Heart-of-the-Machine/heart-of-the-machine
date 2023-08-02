package com.github.hotm.mod.node.aura

import java.util.function.Supplier
import com.github.hotm.mod.Constants.id
import net.minecraft.nbt.NbtElement
import com.kneelawk.graphlib.api.graph.user.LinkKey
import com.kneelawk.graphlib.api.graph.user.LinkKeyType

object AuraLinkKey : LinkKey {
    val TYPE = LinkKeyType.of(id("aura"), Supplier { AuraLinkKey })

    override fun getType(): LinkKeyType = TYPE

    override fun toTag(): NbtElement? = null
}
