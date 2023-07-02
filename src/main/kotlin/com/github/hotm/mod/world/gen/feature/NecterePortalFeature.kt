package com.github.hotm.mod.world.gen.feature

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.blockentity.NecterePortalSpawnerBlockEntity
import com.github.hotm.mod.util.WorldUtils
import com.github.hotm.mod.world.HotMDimensions
import com.github.hotm.mod.world.HotMPortalGenPositions
import com.mojang.serialization.Codec
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext

class NecterePortalFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    override fun place(
        ctx: FeatureContext<DefaultFeatureConfig>
    ): Boolean {
        val world = ctx.world
        val pos = HotMPortalGenPositions.getPortalSpawnerPos(world, ChunkPos(ctx.origin))
        val serverWorld = WorldUtils.getServerWorld(world)

        return if (serverWorld?.registryKey != HotMDimensions.NECTERE_KEY && serverWorld?.structureManager?.shouldGenerate() == true) {
            val originalBlock = world.getBlockState(pos)

            // already generated in this chunk
            if (originalBlock.isOf(HotMBlocks.NECTERE_PORTAL_SPAWNER)) return true

            world.setBlockState(pos, HotMBlocks.NECTERE_PORTAL_SPAWNER.defaultState, 3)
            (world.getBlockEntity(pos) as? NecterePortalSpawnerBlockEntity)?.let { be ->
                be.originalBlock = originalBlock
            }

            true
        } else {
            false
        }
    }
}
