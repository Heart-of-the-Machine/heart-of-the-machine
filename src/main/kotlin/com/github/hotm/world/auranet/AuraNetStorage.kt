package com.github.hotm.world.auranet

import com.github.hotm.world.storage.CustomSerializingRegionBasedStorage
import com.mojang.datafixers.DataFixer
import net.minecraft.util.math.ChunkSectionPos
import java.io.File

class AuraNetStorage(file: File, dataFixer: DataFixer, dsync: Boolean) :
    CustomSerializingRegionBasedStorage<AuraNetData>(
        file,
        AuraNetData.Companion::createCodec,
        ::AuraNetData,
        dataFixer,
        null,
        dsync
    ) {
    fun getBaseAura(pos: ChunkSectionPos): Float {
        return getOrCreate(pos.asLong()).baseValue
    }

    fun setBaseAura(pos: ChunkSectionPos, baseAura: Float) {
        getOrCreate(pos.asLong()).baseValue = baseAura
    }

//    fun add(pos: BlockPos, node: AuraNetNode) {
//        getOrCreate(ChunkSectionPos.from(pos).asLong()).add(pos, node)
//    }
//
//    operator fun get(pos: BlockPos): Optional<AuraNetNode> {
//        return get(ChunkSectionPos.from(pos).asLong()).flatMap { it[pos] }
//    }
//
//    fun remove(pos: BlockPos) {
//        getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos)
//    }
}