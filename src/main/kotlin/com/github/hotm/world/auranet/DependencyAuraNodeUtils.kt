package com.github.hotm.world.auranet

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.util.WorldUtils
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockStateRaycastContext
import net.minecraft.world.World

/**
 * Object for helping with connecting and disconnecting parent and child nodes.
 */
object DependencyAuraNodeUtils {
    /**
     * Connects a parent and child aura node.
     *
     * @return whether the connection was successful.
     */
    fun connect(world: World?, parent: DependableAuraNode, child: DependantAuraNode): ConnectionError {
        if (parent.dimPos.dim != child.dimPos.dim) {
            return ConnectionError.WRONG_DIMENSION
        }

        val maxDistance = parent.maxDistance + child.maxDistance
        if (!parent.pos.isWithinDistance(child.pos, maxDistance)) {
            return ConnectionError.TOO_FAR
        }

        if (world != null && raycastConnection(world, parent.pos, child.pos)) {
            return ConnectionError.BLOCKED
        }

        if (!parent.isChildValid(child)) {
            return ConnectionError.REJECTED_CHILD
        }

        if (!child.isParentValid(parent)) {
            return ConnectionError.REJECTED_PARENT
        }

        if (child.wouldCauseDependencyLoop(parent.dimPos)) {
            return ConnectionError.DEPENDENCY_LOOP
        }

        parent.addChild(child)
        child.addParent(parent)

        parent.onChildAdded(child)
        child.onParentAdded(parent)

        child.recalculateDescendants()

        return ConnectionError.NONE
    }

    /**
     * Disconnects a parent and child aura node.
     */
    fun disconnect(parent: DependableAuraNode, child: DependantAuraNode) {
        parent.removeChild(child.pos)
        child.removeParent(parent.pos)

        parent.onChildRemoved(child.pos)
        child.onParentRemoved(parent.pos)

        child.recalculateDescendants()
    }

    /**
     * Disconnects a parent and child aura node. This is useful for parent aura nodes.
     */
    fun parentDisconnect(childPos: BlockPos?, access: AuraNetAccess, parent: DependableAuraNode) {
        AuraNodeUtils.nodeAt<DependantAuraNode>(childPos, access)?.let { child ->
            disconnect(parent, child)
        }
    }

    /**
     * Disconnects a parent and all children in the collection. This is useful for parent aura nodes.
     */
    fun parentDisconnectAll(children: Collection<BlockPos>, access: AuraNetAccess, parent: DependableAuraNode) {
        val childrenCopy = children.toList()
        for (child in childrenCopy) {
            parentDisconnect(child, access, parent)
        }
    }

    /**
     * Disconnects a parent and child aura node. This is useful for child aura nodes.
     */
    fun childDisconnect(parentPos: BlockPos?, access: AuraNetAccess, child: DependantAuraNode) {
        AuraNodeUtils.nodeAt<DependableAuraNode>(parentPos, access)?.let { parent ->
            disconnect(parent, child)
        }
    }

    /**
     * Disconnects a child aura node from all parents in the collection. This is useful for child aura nodes.
     */
    fun childDisconnectAll(parents: Collection<BlockPos>, access: AuraNetAccess, child: DependantAuraNode) {
        val parentsCopy = parents.toList()
        for (parent in parentsCopy) {
            childDisconnect(parent, access, child)
        }
    }

    /**
     * Raycasts to each child of the aura node to see if there are any blocks blocking the connection.
     *
     * Note, this is a somewhat expensive operation and should only be performed once a second on average.
     */
    fun updateConnections(world: World, parentPos: BlockPos) {
        if (world !is ServerWorld) return
        val storage = StorageUtils.getServerAuraNetStorage(world)
        val parent = storage[parentPos] as? DependableAuraNode ?: return

        if (!parent.blockable) return

        val children = parent.getChildren().toList()

        for (child in children) {
            if (raycastConnection(world, parentPos, child)) {
                parentDisconnect(child, storage, parent)
            }
        }
    }

    /**
     * Raycasts between two blocks to see if a connection between them is blocked.
     *
     * Note, this method avoids loading chunks by only performing the raycast if both ends are in already loaded chunks.
     *
     * @return true if a blockage was detected, false otherwise.
     */
    fun raycastConnection(world: World, start: BlockPos, end: BlockPos): Boolean {
        if (!world.isChunkLoaded(start) || !world.isChunkLoaded(end)) {
            return false
        }

        val res =
            WorldUtils.loadedRaycast(
                world,
                BlockStateRaycastContext(Vec3d.ofCenter(start), Vec3d.ofCenter(end), BlockState::isOpaque)
            )

        return res.type == HitResult.Type.BLOCK && res.blockPos != start && res.blockPos != end
    }

    /**
     * Describes whether there was an error while connecting aura nodes.
     */
    enum class ConnectionError {
        /**
         * Indicates that the connection was successful.
         */
        NONE,

        /**
         * Indicates that the parent and child nodes were in different dimensions.
         */
        WRONG_DIMENSION,

        /**
         * Indicates that the parent and child nodes are too far apart to connect.
         */
        TOO_FAR,

        /**
         * Indicates that there are blocks between the parent and child nodes preventing them from connecting.
         */
        BLOCKED,

        /**
         * Indicates that the parent aura node rejected the child.
         */
        REJECTED_CHILD,

        /**
         * Indicates that the child aura node rejected the parent.
         */
        REJECTED_PARENT,

        /**
         * Indicates that the connection could not be performed due to a dependency loop.
         */
        DEPENDENCY_LOOP;
    }
}