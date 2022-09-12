package com.github.hotm.client.blockmodel

import com.github.hotm.HotMConstants
import com.github.hotm.client.HotMClientRegistries
import com.github.hotm.client.blockmodel.connector.IdentityModelConnector
import com.github.hotm.client.blockmodel.connector.LeylineModelConnector
import com.github.hotm.client.blockmodel.ct.UnbakedCTModelLayer
import com.github.hotm.client.blockmodel.static.UnbakedStaticBottomTopModelLayer
import com.github.hotm.client.blockmodel.static.UnbakedStaticColumnModelLayer
import com.github.hotm.client.blockmodel.static.UnbakedStaticModelLayer
import com.github.hotm.misc.HotMLog
import com.google.gson.JsonParseException
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.ModelResourceProvider
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.io.IOException
import java.io.InputStreamReader

object HotMBlockModels {
    val AURA_NODE_BEAM_CROWN_PIECE = ModelIdentifier(HotMConstants.identifier("aura_node_beam_crown_piece"), "")

    fun register() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider { _, out ->
            out.accept(AURA_NODE_BEAM_CROWN_PIECE)
        }

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
            HotMClientRegistries.BLOCK_MODEL_LAYER,
            HotMConstants.identifier("static_bottom_top"),
            UnbakedStaticBottomTopModelLayer.CODEC
        )
        Registry.register(
            HotMClientRegistries.BLOCK_MODEL_LAYER,
            HotMConstants.identifier("static_column"),
            UnbakedStaticColumnModelLayer.CODEC
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
            val resource = rm.getResource(modelResource)
            if (resource.isPresent) {
                try {
                    resource.get().open().use { stream ->
                        val jsonObject =
                            JsonHelper.deserialize(InputStreamReader(stream))

                        UnbakedModel.CODEC.parse(JsonOps.INSTANCE, jsonObject).resultOrPartial(HotMLog.log::warn)
                            .orElse(null)
                    }
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
