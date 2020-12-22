package com.github.hotm.datafixer

import com.github.hotm.HotMConstants
import com.github.hotm.datafixer.schema.Schema99
import com.mojang.datafixers.DataFixerBuilder
import net.minecraft.util.Util

object HotMSchemas {
    val FIXER = DataFixerBuilder(HotMConstants.DATA_VERSION).also { build(it) }.build(Util.getBootstrapExecutor())

    fun setup() {
//        DataFixerHelper.INSTANCE.
    }

    private fun build(builder: DataFixerBuilder) {
        builder.addSchema(99, ::Schema99)
    }
}