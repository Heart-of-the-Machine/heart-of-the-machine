package com.github.hotm.mod.world.aura

import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.mixin.api.ServerAuraStorageAccess
import com.github.hotm.mod.world.aura.server.ServerAuraStorage
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkSectionPos

object Aura {
    private fun getServerStorage(world: ServerWorld): ServerAuraStorage {
        return (world.chunkManager.delegate as ServerAuraStorageAccess).hotm_getAuraStorage()
    }

    fun getBase(world: ServerWorld, pos: ChunkSectionPos): Float {
        return getServerStorage(world).getBase(pos)
    }

    fun get(world: ServerWorld, pos: ChunkSectionPos): Float {
        return getServerStorage(world).get(pos)
    }

    fun recalculate(world: ServerWorld, pos: ChunkSectionPos) {
        getServerStorage(world).recalculate(pos)
    }

    fun init() {
        ServerChunkEvents.CHUNK_LOAD.register { world, chunk ->
            try {
                getServerStorage(world).onWorldChunkLoad(chunk.pos)
            } catch (e: Exception) {
                HotMLog.LOG.error(
                    "Error loading aura chunk. World: '{}'/{}, Chunk: {}",
                    world,
                    world.registryKey.value,
                    chunk.pos,
                    e
                )
            }
        }
        ServerChunkEvents.CHUNK_UNLOAD.register { world, chunk ->
            try {
                val storage = getServerStorage(world)
                storage.saveChunk(chunk.pos)
                storage.onWorldChunkUnload(chunk.pos)
            } catch (e: Exception) {
                HotMLog.LOG.error(
                    "Error unloading aura chunk. World: '{}'/{}, Chunk: {}",
                    world,
                    world.registryKey.value,
                    chunk.pos,
                    e
                )
            }
        }
        ServerWorldTickEvents.END.register { _, world ->
            try {
                getServerStorage(world).tick()
            } catch (e: Exception) {
                HotMLog.LOG.error("Error ticking aura storage. World: '{}'/{}", world, world.registryKey.value, e)
            }
        }
        ServerWorldLoadEvents.UNLOAD.register { _, world ->
            try {
                getServerStorage(world).close()
            } catch (e: Exception) {
                HotMLog.LOG.error("Error closing aura storage. World: '{}'/{}", world, world.registryKey.value, e)
            }
        }
    }
}
