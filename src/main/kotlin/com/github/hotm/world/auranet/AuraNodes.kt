package com.github.hotm.world.auranet

import com.github.hotm.HotMConstants
import com.github.hotm.misc.HotMRegistries
import net.minecraft.util.registry.Registry

object AuraNodes {
    fun register() {
        register("basic_siphon", BasicSiphonAuraNode.Type)
    }

    fun register(id: String, codec: AuraNodeType<out AuraNode>) {
        Registry.register(HotMRegistries.AURA_NODE_TYPE, HotMConstants.identifier(id), codec)
    }
}