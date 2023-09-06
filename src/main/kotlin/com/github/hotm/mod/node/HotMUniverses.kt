package com.github.hotm.mod.node

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.auranet.CollectorDistributorAuraNode
import com.github.hotm.mod.auranet.SimpleSiphonAuraNode
import com.github.hotm.mod.auranet.SimpleSourceAuraNode
import com.github.hotm.mod.node.aura.AuraLinkEntity
import com.github.hotm.mod.node.aura.AuraLinkKey
import com.github.hotm.mod.node.aura.AuraNodeBlockDiscoverer
import com.github.hotm.mod.node.aura.CollectorDistributorAuraBlockNode
import com.github.hotm.mod.node.aura.SimpleSiphonAuraBlockNode
import com.github.hotm.mod.node.aura.SimpleSourceAuraBlockNode
import com.kneelawk.graphlib.api.graph.GraphUniverse
import com.kneelawk.graphlib.api.graph.user.SyncProfile

object HotMUniverses {
    val NETWORKS = GraphUniverse.builder().synchronizeToClient(SyncProfile.SYNC_EVERYTHING).build(id("networks"))

    fun init() {
        NETWORKS.register()

        NETWORKS.addDiscoverer(AuraNodeBlockDiscoverer)
        NETWORKS.addLinkKeyType(AuraLinkKey.TYPE)
        NETWORKS.addLinkEntityType(AuraLinkEntity.TYPE)

        NETWORKS.addNodeTypes(
            SimpleSiphonAuraBlockNode.TYPE,
            SimpleSourceAuraBlockNode.TYPE,
            CollectorDistributorAuraBlockNode.TYPE
        )
        NETWORKS.addNodeEntityTypes(
            SimpleSiphonAuraNode.TYPE,
            SimpleSourceAuraNode.TYPE,
            CollectorDistributorAuraNode.TYPE
        )
    }
}
