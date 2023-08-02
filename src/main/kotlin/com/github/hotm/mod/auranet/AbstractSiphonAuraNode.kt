package com.github.hotm.mod.auranet

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkSectionPos

abstract class AbstractSiphonAuraNode : AbstractAuraNode() {
    override fun onAdded() {
        if (context.blockWorld is ServerWorld) {
            AuraNodeUtils.updateAllSiphons(context.graphWorld, ChunkSectionPos.from(context.blockPos))
        }
    }

    override fun onDelete() {
        if (context.blockWorld is ServerWorld) {
            AuraNodeUtils.updateAllSiphons(context.graphWorld, ChunkSectionPos.from(context.blockPos))
        }
    }
}
