package com.github.hotm.meta

import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.meta.MetaAccess
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

abstract class AbstractMetaBlock(
    override val type: MetaBlockType<out MetaBlock>, protected val access: MetaAccess,
    private val updateListener: Runnable?, pos: BlockPos
) : MetaBlock {

    /**
     * This meta block's location in its world.
     */
    final override val pos: BlockPos = pos.toImmutable()

    /**
     * Indicates whether this meta block is on the client-side or server-side.
     */
    protected val isClient = access.isClient

    /**
     * Access to the world with this node is associated.
     *
     * WARNING: Be careful not to unnecessarily load chunks as avoiding this is the sole purpose of using MetaBlocks instead of BlockEntities.
     * Things that cause chunk loads are things like getting BlockEntities or BlockStates.
     *
     * The primary purpose of this field is to get the players watching a location.
     */
    protected val world: World
        get() = access.world

    /**
     * This meta block's dimension world key.
     */
    val dimension: RegistryKey<World> = world.registryKey

    /**
     * Provides the location of this meta block, including dimension.
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

        other as AbstractMetaBlock

        if (dimPos != other.dimPos) return false

        return true
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }
}