package com.github.hotm.world.auranet

import com.github.hotm.util.DimBlockPos
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

abstract class AbstractAuraNode(
    override val type: AuraNodeType<out AuraNode>, protected val access: AuraNetAccess,
    private val updateListener: Runnable?, pos: BlockPos
) : AuraNode {

    /**
     * This aura node's location in its world.
     */
    final override val pos: BlockPos = pos.toImmutable()

    /**
     * Indicates whether this aura-node is on the client-side or server-side.
     */
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

    /**
     * This aura node's dimension world key.
     */
    val dimension: RegistryKey<World> = world.registryKey

    /**
     * Provides the location of this aura node, including dimension.
     */
    override val dimPos = DimBlockPos(world.registryKey, pos)

    /**
     * Indicates to the node storage that this node has been changed and should be re-saved to disk.
     */
    fun markDirty() {
        updateListener?.run()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractAuraNode

        if (dimPos != other.dimPos) return false

        return true
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }
}