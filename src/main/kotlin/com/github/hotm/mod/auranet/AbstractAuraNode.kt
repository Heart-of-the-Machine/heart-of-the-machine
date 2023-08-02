package com.github.hotm.mod.auranet

import net.minecraft.server.world.ServerWorld
import com.kneelawk.graphlib.api.graph.user.AbstractNodeEntity

abstract class AbstractAuraNode : AbstractNodeEntity(), AuraNode {
    override fun onAdded() {
        if (context.blockWorld is ServerWorld) {
            AuraNodeUtils.updateValues(context.holder)
        }
    }
}
