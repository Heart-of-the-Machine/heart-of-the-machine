package com.github.hotm.client.blockmodel

import com.github.hotm.client.HotMClientRegistries
import com.mojang.serialization.Codec
import java.util.function.Function
import net.minecraft.client.render.model.UnbakedModel as MCUnbakedModel

interface UnbakedModel : MCUnbakedModel {
    companion object {
        val CODEC: Codec<UnbakedModel> =
            HotMClientRegistries.BLOCK_MODEL.codec.dispatch(UnbakedModel::codec, Function.identity())
    }

    val codec: Codec<out UnbakedModel>
}
