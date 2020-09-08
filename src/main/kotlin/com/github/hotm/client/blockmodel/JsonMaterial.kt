package com.github.hotm.client.blockmodel

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import java.util.*

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
                    }, { it.name.toLowerCase() }).optionalFieldOf("blend_mode")
                        .forGetter { Optional.of(it.blendMode) },
                    Codec.BOOL.optionalFieldOf("enable_ambient_occlusion")
                        .forGetter { Optional.of(it.enableAmbientOcclusion) },
                    Codec.BOOL.optionalFieldOf("enable_color_index").forGetter { Optional.of(it.enableColorIndex) },
                    Codec.BOOL.optionalFieldOf("enable_diffuse_shading")
                        .forGetter { Optional.of(it.enableDiffuseShading) },
                    Codec.BOOL.optionalFieldOf("emissive").forGetter { Optional.of(it.emissive) }
                )
                    .apply(instance) { blendMode, enableAmbientOcclusion, enableColorIndex, enableDiffuseShading, emissive ->
                        JsonMaterial(
                            blendMode.orElse(BlendMode.DEFAULT),
                            enableAmbientOcclusion.orElse(true),
                            enableColorIndex.orElse(true),
                            enableDiffuseShading.orElse(true),
                            emissive.orElse(false)
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
