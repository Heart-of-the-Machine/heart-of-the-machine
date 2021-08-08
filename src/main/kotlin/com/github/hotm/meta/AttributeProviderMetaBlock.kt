package com.github.hotm.meta

import alexiil.mc.lib.attributes.AttributeList

/**
 * Called by AbstractBlockWithMeta when retrieving attributes.
 */
interface AttributeProviderMetaBlock : MetaBlock {
    fun addAllAttributes(to: AttributeList<*>)
}