package com.github.hotm.meta.auranet

import com.github.hotm.meta.MetaBlock

interface AuraNode : MetaBlock {
    fun getValue(): Float
}