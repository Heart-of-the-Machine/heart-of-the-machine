package com.github.hotm.auranet

interface PortalTXAuraNode : AuraNode {
    fun isValid(): Boolean

    fun getSuppliedAura(): Float
}