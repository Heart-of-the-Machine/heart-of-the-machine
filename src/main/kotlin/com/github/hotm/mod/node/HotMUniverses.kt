package com.github.hotm.mod.node

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.auranet.SimpleSiphonAuraNode
import com.github.hotm.mod.auranet.SimpleSourceAuraNode
import com.github.hotm.mod.node.aura.AuraLinkEntity
import com.github.hotm.mod.node.aura.AuraLinkKey
import com.github.hotm.mod.node.aura.AuraNodeBlockDiscoverer
import com.github.hotm.mod.node.aura.SimpleSiphonAuraBlockNode
import com.github.hotm.mod.node.aura.SimpleSourceAuraBlockNode
import com.kneelawk.graphlib.api.graph.GraphUniverse
import com.kneelawk.graphlib.api.graph.user.SyncProfile

object HotMUniverses {
    val AURA = GraphUniverse.builder().synchronizeToClient(SyncProfile.SYNC_EVERYTHING).build(id("auranet"))

    fun init() {
        AURA.register()

        AURA.addDiscoverer(AuraNodeBlockDiscoverer)
        AURA.addLinkKeyType(AuraLinkKey.TYPE)
        AURA.addLinkEntityType(AuraLinkEntity.TYPE)

        AURA.addNodeTypes(SimpleSiphonAuraBlockNode.TYPE, SimpleSourceAuraBlockNode.TYPE)
        AURA.addNodeEntityTypes(SimpleSiphonAuraNode.TYPE, SimpleSourceAuraNode.TYPE)
    }
}
