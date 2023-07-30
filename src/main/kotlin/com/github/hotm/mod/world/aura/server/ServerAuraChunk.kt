package com.github.hotm.mod.world.aura.server

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.ChunkSectionPos
import com.kneelawk.graphlib.api.world.StorageChunk

class ServerAuraChunk(
    private val pos: ChunkSectionPos, private val markDirty: Runnable, baseAura: Float, currentAura: Float
) : StorageChunk {
    var baseAura = baseAura
        set(value) {
            field = value
            markDirty.run()
        }

    var currentAura = currentAura
        set(value) {
            field = value
            markDirty.run()
        }

    constructor(pos: ChunkSectionPos, markDirty: Runnable, initialAura: Float) : this(
        pos,
        markDirty,
        initialAura,
        initialAura
    )

    constructor(compound: NbtCompound, pos: ChunkSectionPos, markDirty: Runnable) : this(
        pos,
        markDirty,
        compound.getFloat("baseAura"),
        compound.getFloat("currentAura")
    )

    override fun toNbt(nbt: NbtCompound) {
        nbt.putFloat("baseAura", baseAura)
        nbt.putFloat("currentAura", currentAura)
    }
}
