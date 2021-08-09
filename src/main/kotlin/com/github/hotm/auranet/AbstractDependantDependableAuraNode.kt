package com.github.hotm.auranet

import com.github.hotm.world.auranet.AuraNetAccess
import net.minecraft.util.math.BlockPos

abstract class AbstractDependantDependableAuraNode(
    type: AuraNodeType<out AuraNode>,
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos
) : AbstractAuraNode(type, access, updateListener, pos), DependantAuraNode, DependableAuraNode {
    abstract fun getParents(): Collection<BlockPos>

    override fun onRemove() {
        disconnectAll()
    }

    protected fun disconnectAll() {
        DependencyAuraNodeUtils.childDisconnectAll(getParents(), access, this)
        DependencyAuraNodeUtils.parentDisconnectAll(getChildren(), access, this)
    }
}