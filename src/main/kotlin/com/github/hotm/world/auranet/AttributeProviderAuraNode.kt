package com.github.hotm.world.auranet

import alexiil.mc.lib.attributes.AttributeList

/**
 * Called by AbstractAuraNodeBlock when retrieving attributes.
 */
interface AttributeProviderAuraNode : AuraNode {
    fun addAllAttributes(to: AttributeList<*>)
}