package com.github.hotm.mod.auranet

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkSectionPos

abstract class AbstractSourceAuraNode : AbstractAuraNode() {
    override fun onDelete() {
        if (context.blockWorld is ServerWorld) {
            AuraNodeUtils.updateChunkAura(context.graphWorld, ChunkSectionPos.from(context.blockPos))
        }
    }
}
