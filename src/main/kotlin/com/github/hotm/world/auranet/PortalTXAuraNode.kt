package com.github.hotm.world.auranet

interface PortalTXAuraNode : AuraNode {
    fun isValid(): Boolean

    fun getSuppliedAura(): Float
}