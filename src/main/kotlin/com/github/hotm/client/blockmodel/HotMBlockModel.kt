package com.github.hotm.client.blockmodel

import com.github.hotm.client.HotMClientRegistries
import com.mojang.serialization.Codec
import net.minecraft.client.render.model.UnbakedModel
import java.util.function.Function

interface HotMBlockModel : UnbakedModel {
    companion object {
        val CODEC: Codec<HotMBlockModel> = HotMClientRegistries.BLOCK_MODEL.dispatch(HotMBlockModel::codec, Function.identity())
    }

    val codec: Codec<out HotMBlockModel>
}