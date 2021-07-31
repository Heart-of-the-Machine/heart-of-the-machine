package com.github.hotm.client

import com.github.hotm.client.blockmodel.HotMBlockModels
import com.github.hotm.client.particle.HotMParticlesClient
import com.github.hotm.client.render.HotMRenderMaterials
import com.github.hotm.client.render.blockentity.HotMBlockEntityRenderers

/**
 * Client mod entry point.
 */
@Suppress("unused")
fun init() {
    HotMClientRegistries.register()
    HotMRenderMaterials.register()
    HotMBlocksClient.register()
    HotMBlockModels.register()
    HotMColorProviders.register()
    HotMBlockEntityRenderers.register()
    HotMParticlesClient.register()
}
