package com.github.hotm.util

import com.github.hotm.misc.HotMLog
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

data class DimBlockPos(val dim: RegistryKey<World>, val pos: BlockPos) {
    companion object {
        fun fromNbt(compound: NbtCompound): DimBlockPos {
            val dimIdent =
                Identifier.CODEC.parse(NbtOps.INSTANCE, compound.get("dim")).resultOrPartial(HotMLog.log::error).get()
            val dim = RegistryKey.of(Registry.WORLD_KEY, dimIdent)
            val posArray = compound.getIntArray("pos")
            val pos = BlockPos(posArray[0], posArray[1], posArray[2])
            return DimBlockPos(dim, pos)
        }
    }

    fun getBlockState(server: MinecraftServer): BlockState? {
        return server.getWorld(dim)?.getBlockState(pos)
    }

    fun getAuraNode(server: MinecraftServer): AuraNode? {
        return server.getWorld(dim)?.let { StorageUtils.getServerAuraNetStorage(it)[pos] }
    }

    fun toNbt(): NbtCompound {
        val compound = NbtCompound()
        Identifier.CODEC.encodeStart(NbtOps.INSTANCE, dim.value).resultOrPartial(HotMLog.log::error).ifPresent {
            compound.put("dim", it)
        }
        compound.putIntArray("pos", intArrayOf(pos.x, pos.y, pos.z))
        return compound
    }
}
