package com.github.hotm.mod.world.aura.server

import java.nio.file.Path
import com.github.hotm.mod.world.HotMDimensions
import com.github.hotm.mod.world.aura.AuraStorage
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import com.kneelawk.graphlib.api.world.SaveMode
import com.kneelawk.graphlib.api.world.UnloadingRegionBasedStorage

class ServerAuraStorage(val world: ServerWorld, data: Path, dsync: Boolean) : AuraStorage, AutoCloseable {
    companion object {
        const val NECTERE_AURA = 16f
        const val NON_NECTERE_AURA = 0f
    }

    private val baseAura = if (world.registryKey == HotMDimensions.NECTERE_KEY) NECTERE_AURA else NON_NECTERE_AURA

    private val chunks = UnloadingRegionBasedStorage(
        world,
        data.resolve("hotm/aura"),
        dsync,
        ::ServerAuraChunk,
        { pos, markDirty -> ServerAuraChunk(pos, markDirty, baseAura) },
        SaveMode.UNLOAD
    )

    override fun getBase(pos: ChunkSectionPos): Float {
        val chunk = chunks.getIfExists(pos) ?: return baseAura
        return chunk.baseAura
    }

    override fun get(pos: ChunkSectionPos): Float {
        val chunk = chunks.getIfExists(pos) ?: return baseAura
        return chunk.currentAura
    }

    override fun update(pos: ChunkSectionPos, value: Float) {
        val chunk = chunks.getOrCreate(pos) ?: return
        chunk.currentAura = value
    }

    fun onWorldChunkLoad(pos: ChunkPos) {
        chunks.onWorldChunkLoad(pos)
    }

    fun onWorldChunkUnload(pos: ChunkPos) {
        chunks.onWorldChunkUnload(pos)
    }

    fun tick() {
        chunks.tick()
    }

    fun saveChunk(pos: ChunkPos) {
        chunks.saveChunk(pos)
    }

    fun saveAll() {
        chunks.saveAll()
    }

    override fun close() {
        chunks.close()
    }
}
