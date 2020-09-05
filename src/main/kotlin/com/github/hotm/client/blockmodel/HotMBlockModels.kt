package com.github.hotm.client.blockmodel

import com.github.hotm.HotMConstants
import com.github.hotm.util.IdentifierUtils.extendPath
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelResourceProvider
import net.minecraft.resource.ResourceManager

object HotMBlockModels {
    fun register() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(HotMBlockModels::getResourceProvider)
    }

    private fun getResourceProvider(rm: ResourceManager): ModelResourceProvider {
        return ModelResourceProvider { model, ctx ->
            // will clean this up later
            if (model == HotMConstants.identifier("block/thinking_glass")) {
                UnbakedConnectedTextureModel(
                    model.extendPath("_none"),
                    model.extendPath("_horizontal"),
                    model.extendPath("_vertical"),
                    model.extendPath("_corner"),
                    model.extendPath("_nocorner")
                )
            } else {
                null
            }
        }
    }
}