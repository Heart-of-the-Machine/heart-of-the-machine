package com.github.hotm.client

import com.github.hotm.client.blockmodel.HotMBlockModels

/**
 * Client mod entry point.
 */
@Suppress("unused")
fun init() {
    HotMClientRegistries.register()
    HotMBlocksClient.register()
    HotMBlockModels.register()
    HotMColorProviders.register()
}
