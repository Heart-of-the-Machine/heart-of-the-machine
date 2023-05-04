package com.github.hotm.mod.world.gen.carver

import java.util.function.Function
import org.apache.commons.lang3.mutable.MutableBoolean
import com.mojang.serialization.Codec
import net.minecraft.registry.Holder
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.carver.CarverContext
import net.minecraft.world.gen.carver.CarvingMask
import net.minecraft.world.gen.carver.CaveCarver
import net.minecraft.world.gen.carver.CaveCarverConfig
import net.minecraft.world.gen.chunk.AquiferSampler

class AirOnlyCaveCarver(codec: Codec<CaveCarverConfig>) : CaveCarver(codec) {
    override fun carveAtPoint(
        context: CarverContext,
        config: CaveCarverConfig,
        chunk: Chunk,
        posToBiome: Function<BlockPos, Holder<Biome>>,
        mask: CarvingMask,
        pos: BlockPos.Mutable,
        downPos: BlockPos.Mutable,
        sampler: AquiferSampler,
        foundSurface: MutableBoolean
    ): Boolean {
        return if (canReplaceBlock(config, chunk.getBlockState(pos)) && chunk.getFluidState(pos).isEmpty) {
            chunk.setBlockState(pos, CAVE_AIR, false)
            true
        } else {
            false
        }
    }
}
