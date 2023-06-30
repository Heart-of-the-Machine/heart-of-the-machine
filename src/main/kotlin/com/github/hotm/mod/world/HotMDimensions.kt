package com.github.hotm.mod.world

import com.github.hotm.mod.Constants.id
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

object HotMDimensions {
    val NECTERE_KEY: RegistryKey<World> = RegistryKey.of(RegistryKeys.WORLD, id("nectere"))

    fun getNectere(server: MinecraftServer): ServerWorld =
        server.getWorld(NECTERE_KEY) ?: throw IllegalStateException("Missing hotm:nectere dimension")
}
