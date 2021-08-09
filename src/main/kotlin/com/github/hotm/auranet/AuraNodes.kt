package com.github.hotm.auranet

import com.github.hotm.HotMConstants
import com.github.hotm.misc.HotMRegistries
import net.minecraft.util.registry.Registry

object AuraNodes {
    fun register() {
        register("basic_siphon", BasicSiphonAuraNode.Type)
        register("basic_source", BasicSourceAuraNode.Type)
        register("collector_distributor", CollectorDistributorAuraNode.Type)
        register("portal_receiver", PortalReceiverAuraNode.Type)
        register("portal_transmitter", PortalTransmitterAuraNode.Type)
    }

    fun register(id: String, codec: AuraNodeType<out AuraNode>) {
        Registry.register(HotMRegistries.AURA_NODE_TYPE, HotMConstants.identifier(id), codec)
    }
}