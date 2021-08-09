package com.github.hotm.auranet

/**
 * An aura node that has a value.
 */
interface ValuedAuraNode {
    /**
     * Gets the value of this aura node.
     *
     * This is generally used by the Aurameter item and some particle effects.
     */
    fun getValue(): Float
}