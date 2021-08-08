package com.github.hotm.meta

import com.github.hotm.HotMConstants
import com.github.hotm.meta.auranet.*
import com.github.hotm.misc.HotMRegistries
import net.minecraft.util.registry.Registry

object MetaBlocks {
    fun register() {
        register("basic_siphon", BasicSiphonAuraNode.Type)
        register("basic_source", BasicSourceAuraNode.Type)
        register("collector_distributor", CollectorDistributorAuraNode.Type)
        register("portal_receiver", PortalReceiverAuraNode.Type)
        register("portal_transmitter", PortalTransmitterAuraNode.Type)
    }

    fun register(id: String, codec: MetaBlockType<out MetaBlock>) {
        Registry.register(HotMRegistries.META_BLOCK_TYPE, HotMConstants.identifier(id), codec)
    }
}