package com.github.hotm.meta.auranet

import com.github.hotm.meta.MetaBlock

interface SourceAuraNode : MetaBlock {
    fun getSourceAura(): Float
}