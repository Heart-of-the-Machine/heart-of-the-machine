package com.github.hotm.auranet

import com.github.hotm.world.auranet.AuraNetAccess
import net.minecraft.util.math.BlockPos

abstract class AbstractDependantAuraNode(
    type: AuraNodeType<out AuraNode>,
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos
) : AbstractAuraNode(type, access, updateListener, pos), DependantAuraNode {
    abstract fun getParents(): Collection<BlockPos>

    override fun onRemove() {
        disconnectAll()
    }

    protected fun disconnectAll() {
        DependencyAuraNodeUtils.childDisconnectAll(getParents(), access, this)
    }
}