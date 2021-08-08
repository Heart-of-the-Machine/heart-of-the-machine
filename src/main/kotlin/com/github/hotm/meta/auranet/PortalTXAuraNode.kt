package com.github.hotm.meta.auranet

import com.github.hotm.meta.MetaBlock

interface PortalTXAuraNode : MetaBlock {
    fun isValid(): Boolean

    fun getSuppliedAura(): Float
}