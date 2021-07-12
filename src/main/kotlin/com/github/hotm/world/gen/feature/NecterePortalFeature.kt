package com.github.hotm.world.gen.feature

import com.github.hotm.blockentity.NecterePortalSpawnerBlockEntity
import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.util.WorldUtils
import com.github.hotm.world.HotMDimensions
import com.github.hotm.world.HotMPortalGenPositions
import com.mojang.serialization.Codec
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext

class NecterePortalFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    override fun generate(
        ctx: FeatureContext<DefaultFeatureConfig>
    ): Boolean {
        val world = ctx.world
        val pos = HotMPortalGenPositions.getPortalSpawnerPos(ChunkPos(ctx.origin))
        val key = WorldUtils.getServerWorld(world)?.registryKey
        val serverWorld = WorldUtils.getServerWorld(world)

        return if (key != HotMDimensions.NECTERE_KEY && serverWorld?.structureAccessor?.shouldGenerateStructures() == true) {
            val originalBlock = world.getBlockState(pos)

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