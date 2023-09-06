package com.github.hotm.mod.client.rendering.aura

import com.github.hotm.mod.auranet.AuraNode
import net.minecraft.util.math.Vec3d

interface AuraNodeRenderer<T : AuraNode> {
    /**
     * Gets the offset of the link pos with respect to the block's origin (0.0, 0.0, 0.0).
     *
     * This is usually (0.5, 0.5, 0.5).
     */
    fun getLinkPosOffset(node: T): Vec3d
}
