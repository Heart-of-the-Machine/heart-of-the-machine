package com.github.hotm.world.gen.feature.segment

/**
 * Determines how a block placement should be handled in terms of leaf decay and distance from sources (logs).
 */
enum class LeafPlacement {
    SOURCE,
    LEAF,
    NONE
}