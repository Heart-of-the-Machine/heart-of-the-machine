package com.github.hotm.client.render.blockentity

import com.github.hotm.blockentity.HotMBlockEntities
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry

object HotMBlockEntityRenderers {
    fun register() {
        BlockEntityRendererRegistry.register(
            HotMBlockEntities.BASIC_SIPHON_AURA_NODE,
            ::SimpleDependableAuraNodeBlockEntityRenderer
        )
        BlockEntityRendererRegistry.register(
            HotMBlockEntities.COLLECTOR_DISTRIBUTOR_AURA_NODE,
            ::SimpleDependableAuraNodeBlockEntityRenderer
        )
        BlockEntityRendererRegistry.register(
            HotMBlockEntities.PORTAL_RECEIVER_AURA_NODE,
            ::SimpleDependableAuraNodeBlockEntityRenderer
        )
    }
}
