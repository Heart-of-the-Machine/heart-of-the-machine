package com.github.hotm.client.blockmodel

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial

data class JsonMaterial(
    val blendMode: BlendMode,
    val enableAmbientOcclusion: Boolean,
    val enableColorIndex: Boolean,
    val enableDiffuseShading: Boolean,
    val emissive: Boolean
) {
    companion object {
        val DEFAULT = JsonMaterial(
            BlendMode.DEFAULT,
            enableAmbientOcclusion = true,
            enableColorIndex = true,
            enableDiffuseShading = true,
            emissive = false
        )

        val CODEC: Codec<JsonMaterial> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<JsonMaterial> ->
                instance.group(
                    Codec.STRING.comapFlatMap({ str ->
                        try {
                            DataResult.success(BlendMode.valueOf(str.toUpperCase()))
                        } catch (e: IllegalArgumentException) {
                            DataResult.error("Unknown blend mode: $str")
                        }
                    }, { it.name.toLowerCase() }).fieldOf("blend_mode").orElse(BlendMode.DEFAULT)
                        .forGetter(JsonMaterial::blendMode),
                    Codec.BOOL.fieldOf("enable_ambient_occlusion").orElse(true)
                        .forGetter(JsonMaterial::enableAmbientOcclusion),
                    Codec.BOOL.fieldOf("enable_color_index").orElse(true).forGetter(JsonMaterial::enableColorIndex),
                    Codec.BOOL.fieldOf("enable_diffuse_shading").orElse(true)
                        .forGetter(JsonMaterial::enableDiffuseShading),
                    Codec.BOOL.fieldOf("emissive").orElse(false).forGetter(JsonMaterial::emissive)
                )
                    .apply(instance) { blendMode, enableAmbientOcclusion, enableColorIndex, enableDiffuseShading, emissive ->
                        JsonMaterial(
                            blendMode,
                            enableAmbientOcclusion,
                            enableColorIndex,
                            enableDiffuseShading,
                            emissive
                        )
                    }
            }
    }

    fun toRenderMaterial(): RenderMaterial {
        return RendererAccess.INSTANCE.renderer.materialFinder()
            .blendMode(0, blendMode)
            .disableAo(0, !enableAmbientOcclusion)
            .disableColorIndex(0, !enableColorIndex)
            .disableDiffuse(0, !enableDiffuseShading)
            .emissive(0, emissive)
            .find()
    }
}
