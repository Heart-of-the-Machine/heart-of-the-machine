package com.github.hotm.dfu

import com.mojang.datafixers.DataFix
import com.mojang.datafixers.TypeRewriteRule
import com.mojang.datafixers.schemas.Schema

class BiomeCategoryFix(outputSchema: Schema, changesType: Boolean) : DataFix(outputSchema, changesType) {
    override fun makeRule(): TypeRewriteRule {
        TODO("Not yet implemented")
    }
}