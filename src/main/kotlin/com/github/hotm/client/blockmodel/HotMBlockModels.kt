package com.github.hotm.client.blockmodel

import com.github.hotm.HotMConstants
import com.github.hotm.HotMLog
import com.github.hotm.client.HotMClientRegistries
import com.github.hotm.client.blockmodel.connector.IdentityModelConnector
import com.github.hotm.client.blockmodel.connector.LeylineModelConnector
import com.github.hotm.client.blockmodel.ct.UnbakedCTModelLayer
import com.github.hotm.client.blockmodel.static.UnbakedStaticModelLayer
import com.google.gson.JsonParseException
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelResourceProvider
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.io.IOException
import java.io.InputStreamReader

object HotMBlockModels {
    fun register() {
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL,
            HotMConstants.identifier("layered"),
            UnbakedLayeredModel.CODEC
        )
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL_LAYER,
            HotMConstants.identifier("quarter_connected_texture"),
            UnbakedCTModelLayer.CODEC
        )
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL_LAYER,
            HotMConstants.identifier("static_all"),
            UnbakedStaticModelLayer.CODEC
        )
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL_CONNECTOR,
            HotMConstants.identifier("identity"),
            IdentityModelConnector
        )
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL_CONNECTOR,
            HotMConstants.identifier("leyline"),
            LeylineModelConnector
        )

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(HotMBlockModels::getResourceProvider)
    }

    private fun getResourceProvider(rm: ResourceManager): ModelResourceProvider {
        return ModelResourceProvider { model, ctx ->
            val modelResource = Identifier(model.namespace, "models/${model.path}.hotm.json")
            if (rm.containsResource(modelResource)) {
                try {
                    val jsonObject =
                        JsonHelper.deserialize(InputStreamReader(rm.getResource(modelResource).inputStream))

                    UnbakedModel.CODEC.parse(JsonOps.INSTANCE, jsonObject).resultOrPartial(HotMLog.log::warn)
                        .orElse(null)
                } catch (e: JsonParseException) {
                    HotMLog.log.warn("Trouble parsing model: $modelResource", e)
                    null
                } catch (e: IOException) {
                    HotMLog.log.warn("Trouble loading model: $modelResource", e)
                    null
                } catch (e: NullPointerException) {
                    HotMLog.log.warn("Trouble parsing model: $modelResource", e)
                    null
                }
            } else {
                null
            }
        }
    }
}