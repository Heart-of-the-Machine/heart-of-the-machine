package com.github.hotm.mod.world.aura

import net.minecraft.util.math.ChunkSectionPos

interface AuraStorage {
    fun getBase(pos: ChunkSectionPos): Float

    fun get(pos: ChunkSectionPos): Float

    fun recalculate(pos: ChunkSectionPos)
}
