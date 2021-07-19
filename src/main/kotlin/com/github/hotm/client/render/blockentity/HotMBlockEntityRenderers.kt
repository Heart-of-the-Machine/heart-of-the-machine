package com.github.hotm.client.render.blockentity

import com.github.hotm.blockentity.HotMBlockEntities
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry

object HotMBlockEntityRenderers {
    fun register() {
        BlockEntityRendererRegistry.INSTANCE.register(
            HotMBlockEntities.BASIC_SIPHON_AURA_NODE,
            ::BasicSiphonAuraNodeBlockEntityRenderer
        )
    }
}