package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractAuraNode(
    override val type: AuraNodeType<out AuraNode>, protected val access: AuraNetAccess,
    private val updateListener: Runnable?, final override val pos: BlockPos
) : AuraNode {
    protected val isClient = access.isClient

    /**
     * Access to the world with this node is associated.
     *
     * WARNING: Be careful not to unnecessarily load chunks as avoiding this is the sole purpose of using AuraNodes instead of BlockEntities.
     * Things that cause chunk loads are things like getting BlockEntities or BlockStates.
     *
     * The primary purpose of this field is to get the players watching a location.
     */
    protected val world: World
        get() = access.world

    override val dimPos = DimBlockPos(world.registryKey, pos)

    fun markDirty() {
        updateListener?.run()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractAuraNode

        if (pos != other.pos) return false

        return true
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }
}