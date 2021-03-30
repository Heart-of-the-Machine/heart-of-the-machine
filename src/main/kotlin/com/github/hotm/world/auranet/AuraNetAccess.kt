package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream

interface AuraNetAccess {
    val isClient: Boolean

    val world: World

    fun getBaseAura(pos: ChunkSectionPos): Int

    operator fun get(pos: BlockPos): AuraNode?

    fun getAllBy(pos: ChunkSectionPos, filter: Predicate<AuraNode>): Stream<AuraNode>
}