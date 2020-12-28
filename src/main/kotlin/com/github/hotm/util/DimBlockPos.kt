package com.github.hotm.util

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.AuraNode
import net.minecraft.block.BlockState
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

data class DimBlockPos(val dim: RegistryKey<World>, val pos: BlockPos) {
    fun getBlockState(server: MinecraftServer): BlockState? {
        return server.getWorld(dim)?.getBlockState(pos)
    }

    fun getAuraNode(server: MinecraftServer): AuraNode? {
        return server.getWorld(dim)?.let { StorageUtils.getAuraNetStorage(it)[pos].orElse(null) }
    }
}
