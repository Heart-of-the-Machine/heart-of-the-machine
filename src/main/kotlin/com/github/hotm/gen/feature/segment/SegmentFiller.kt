package com.github.hotm.gen.feature.segment

import com.github.hotm.gen.feature.segment.FeatureSegmentUtils.place
import com.github.hotm.gen.feature.segment.FeatureSegmentUtils.tryPlace
import com.terraformersmc.shapes.api.Filler
import com.terraformersmc.shapes.api.Position
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView

/**
 * A kind of Filler that just adds block placements to a Segment's blocks to place.
 */
class SegmentFiller(
    private val blocks: MutableMap<BlockPos, BlockPlacement>,
    private val placement: BlockPlacement
) : Filler {
    override fun accept(t: Position) {
        place(blocks, t.toBlockPos(), placement)
    }
}

/**
 * Tries to fill an area but stops if it encounters a block existing in the world.
 */
class SegmentTryFiller(
    private val blocks: MutableMap<BlockPos, BlockPlacement>,
    private val world: WorldView,
    private val placement: BlockPlacement
) : Filler {
    /**
     * Whether all blocks were filled successfully.
     */
    var success = true
        private set

    override fun accept(t: Position) {
        if (success) {
            if (!tryPlace(blocks, world, t.toBlockPos(), placement)) {
                success = false
            }
        }
    }
}
