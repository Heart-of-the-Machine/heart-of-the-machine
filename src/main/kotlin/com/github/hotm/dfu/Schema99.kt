package com.github.hotm.dfu

import com.mojang.datafixers.schemas.Schema
import com.mojang.datafixers.types.templates.TypeTemplate
import java.util.function.Supplier

class Schema99(versionKey: Int, parent: Schema) : Schema(versionKey, parent) {
    override fun registerTypes(
        schema: Schema,
        entityTypes: MutableMap<String, Supplier<TypeTemplate>>,
        blockEntityTypes: MutableMap<String, Supplier<TypeTemplate>>
    ) {
        super.registerTypes(schema, entityTypes, blockEntityTypes)
    }
}