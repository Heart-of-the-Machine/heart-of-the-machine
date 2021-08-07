package com.github.hotm.blockentity

import com.github.hotm.util.lazyVar
import com.github.hotm.world.auranet.AuraNodeUtils
import com.github.hotm.world.auranet.PortalTransmitterAuraNode
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PortalTransmitterAuraNodeBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HotMBlockEntities.PORTAL_TRANSMITTER_AURA_NODE, pos, state) {
    companion object {
        /**
         * The period of time between portal aura node connection checks. 20 ticks = 1 second.
         */
        private const val PORTAL_CONNECTION_UPDATE_WAIT = 20

        fun tickServer(world: World, pos: BlockPos, state: BlockState, entity: PortalTransmitterAuraNodeBlockEntity) {
            entity.updateConnections()
        }
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

    fun updateConnections() {
        val world = world ?: return
        val time = world.time

        if (time - lastPortalConnectionUpdate >= PORTAL_CONNECTION_UPDATE_WAIT) {
            lastPortalConnectionUpdate = time

            AuraNodeUtils.nodeAt<PortalTransmitterAuraNode>(pos, world)?.recalculateValidity()
        }
    }
}