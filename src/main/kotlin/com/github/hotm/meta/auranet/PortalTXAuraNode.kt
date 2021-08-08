package com.github.hotm.meta.auranet

interface PortalTXAuraNode : AuraNode {
    fun isValid(): Boolean

    fun getSuppliedAura(): Float
}