package com.github.hotm.blockentity

import com.github.hotm.util.lazyVar
import com.github.hotm.world.auranet.AuraNodeUtils
import com.github.hotm.world.auranet.PortalReceiverAuraNode
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class PortalReceiverAuraNodeBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractDependableAuraNodeBlockEntity(HotMBlockEntities.PORTAL_RECEIVER_AURA_NODE, pos, state) {

    companion object {
        /**
         * The period of time between portal aura node connection checks. 20 ticks = 1 second.
         */
        private const val PORTAL_CONNECTION_UPDATE_WAIT = 20
    }

    private var lastPortalConnectionUpdate by lazyVar { world?.time ?: 0L }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        lastPortalConnectionUpdate = if (nbt.contains("lastPortalConnectionUpdate")) {
            nbt.getLong("lastPortalConnectionUpdate")
        } else {
            world?.time ?: 0L
        }
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val new = super.writeNbt(nbt)
        new.putLong("lastPortalConnectionUpdate", lastPortalConnectionUpdate)
        return new
    }

    override fun updateConnections() {
        super.updateConnections()

        val world = world ?: return
        val time = world.time

        if (time - lastPortalConnectionUpdate >= PORTAL_CONNECTION_UPDATE_WAIT) {
            lastPortalConnectionUpdate = time

            AuraNodeUtils.nodeAt<PortalReceiverAuraNode>(pos, world)?.recalculateValidity()
        }
    }
}
