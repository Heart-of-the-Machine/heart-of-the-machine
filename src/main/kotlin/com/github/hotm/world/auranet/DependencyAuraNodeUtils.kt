package com.github.hotm.world.auranet

import net.minecraft.util.math.BlockPos

/**
 * Object for helping with connecting and disconnecting parent and child nodes.
 */
object DependencyAuraNodeUtils {
    /**
     * Connects a parent and child aura node.
     *
     * @return whether the connection was successful.
     */
    fun connect(parent: DependableAuraNode, child: DependantAuraNode): ConnectionError {
        if (parent.dimPos.dim != child.dimPos.dim) {
            return ConnectionError.WRONG_DIMENSION
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
    fun parentDisconnect(pos: BlockPos?, access: AuraNetAccess, parent: DependableAuraNode) {
        AuraNodeUtils.nodeAt<DependantAuraNode>(pos, access)?.let { child ->
            disconnect(parent, child)
        }
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