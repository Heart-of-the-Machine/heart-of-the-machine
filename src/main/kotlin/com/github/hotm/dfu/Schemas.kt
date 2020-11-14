package com.github.hotm.dfu

import com.github.hotm.HotMConstants
import com.mojang.datafixers.DataFixer
import com.mojang.datafixers.DataFixerBuilder
import net.minecraft.util.Util

object Schemas {
    fun create(): DataFixer {
        val builder = DataFixerBuilder(HotMConstants.DATA_VERSION)

        val schema99 = builder.addSchema(99, ::Schema99)
        val schema100 = builder.addSchema(100, ::Schema100)
        builder.addFixer(BiomeCategoryFix(schema100, false))

        return builder.build(Util.getBootstrapExecutor())
    }
}