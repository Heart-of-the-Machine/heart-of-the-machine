package com.github.hotm.gen.feature

import com.github.hotm.HotMBlocks
import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import java.util.*

class NecterePortalFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    override fun generate(
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: DefaultFeatureConfig
    ): Boolean {
        val originalBlock = world.getBlockState(pos)

        world.setBlockState(pos, HotMBlocks.NECTERE_PORTAL_SPAWNER.defaultState, 3)
        (world.getBlockEntity(pos) as? NecterePortalSpawnerBlockEntity)?.let { be ->
            be.originalBlock = originalBlock
        }

        return true
    }
}