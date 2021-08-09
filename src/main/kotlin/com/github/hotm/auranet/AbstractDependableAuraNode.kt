package com.github.hotm.auranet

import com.github.hotm.world.auranet.AuraNetAccess
import net.minecraft.util.math.BlockPos

abstract class AbstractDependableAuraNode(
    type: AuraNodeType<out AuraNode>,
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos
) : AbstractAuraNode(type, access, updateListener, pos), DependableAuraNode {
    override fun onRemove() {
        disconnectAll()
    }

    protected fun disconnectAll() {
        DependencyAuraNodeUtils.parentDisconnectAll(getChildren(), access, this)
    }
}