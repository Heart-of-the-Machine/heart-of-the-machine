package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import java.util.function.Predicate
import java.util.stream.Stream

interface AuraNetAccess {
    /**
     * Indicates whether this aura net access is on the logical client or the server.
     */
    val isClient: Boolean

    /**
     * Gets the world associated with this aura net access.
     */
    val world: World

    /**
     * Gets the update listener for a particular chunk section.
     *
     * This is generally used for aura node creation.
     */
    fun getUpdateListener(pos: ChunkSectionPos): Runnable?

    /**
     * Gets the base aura value in a chunk section.
     *
     * This is the aura value without any source or siphon calculations.
     */
    fun getBaseAura(pos: ChunkSectionPos): Int

    /**
     * Gets the aura node at a particular block position if it exists.
     */
    operator fun get(pos: BlockPos): AuraNode?

    /**
     * Gets all the aura nodes in a chunk section depending on a filter predicate.
     */
    fun getAllBy(pos: ChunkSectionPos, filter: Predicate<AuraNode>): Stream<AuraNode>
}