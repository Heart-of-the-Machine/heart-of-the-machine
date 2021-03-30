package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos

interface DependableAuraNode {
    fun getDependants(): Collection<DimBlockPos>
}