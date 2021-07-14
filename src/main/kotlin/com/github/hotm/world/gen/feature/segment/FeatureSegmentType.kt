package com.github.hotm.world.gen.feature.segment

import com.mojang.serialization.Codec

/**
 * A type of feature part.
 */
class FeatureSegmentType<C, S : FeatureSegment<C>>(val codec: Codec<S>)
