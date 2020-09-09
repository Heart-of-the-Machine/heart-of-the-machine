package com.github.hotm.client

import com.github.hotm.client.blockmodel.HotMBlockModels

/**
 * Client mod entry point.
 */
@Suppress("unused")
fun init() {
    HotMBlocksClient.register()
    HotMBlockModels.register()
    HotMColorProviders.register()
}
