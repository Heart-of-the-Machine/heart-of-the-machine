package com.github.hotm.datafixer.schema

import com.github.hotm.datafixer.HotMTypeReferences
import com.mojang.datafixers.DSL
import com.mojang.datafixers.schemas.Schema
import com.mojang.datafixers.types.templates.TypeTemplate
import java.util.function.Supplier

class Schema99(versionKey: Int, parent: Schema) : Schema(versionKey, parent) {
    override fun registerTypes(
        schema: Schema,
        entityTypes: MutableMap<String, Supplier<TypeTemplate>>,
        blockEntityTypes: MutableMap<String, Supplier<TypeTemplate>>
    ) {
        schema.registerType(false, HotMTypeReferences.AURA_NET_CHUNK, DSL::remainder)
    }
}