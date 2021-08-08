package com.github.hotm.world.meta

import com.github.hotm.meta.MetaBlock
import com.github.hotm.util.DimBlockPos
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import java.util.function.Predicate
import java.util.stream.Stream

interface MetaAccess {
    /**
     * Indicates whether this meta access is on the logical client or the server.
     */
    val isClient: Boolean

    /**
     * Gets the world associated with this meta access.
     */
    val world: World

    /**
     * Gets the update listener for the chunk section containing this particular block pos.
     *
     * This is generally used for meta creation.
     */
    fun getUpdateListener(pos: BlockPos): Runnable? {
        return getUpdateListener(ChunkSectionPos.from(pos))
    }

    /**
     * Gets the update listener for a particular chunk section.
     *
     * This is generally used for meta creation.
     */
    fun getUpdateListener(pos: ChunkSectionPos): Runnable?

    /**
     * Gets the base aura value in a chunk section.
     *
     * This is the aura value without any source or siphon calculations.
     */
    fun getBaseAura(pos: ChunkSectionPos): Float

    /**
     * Gets the meta block at a particular block position if it exists.
     */
    operator fun get(pos: BlockPos): MetaBlock?

    /**
     * Gets all the meta blocks in a chunk section depending on a filter predicate.
     */
    fun getAllBy(pos: ChunkSectionPos, filter: Predicate<MetaBlock>): Stream<MetaBlock>

    /**
     * Causes this meta access to recalculate all siphons within a chunk section.
     */
    fun recalculateSiphons(pos: ChunkSectionPos, visitedNodes: MutableSet<DimBlockPos>)
}