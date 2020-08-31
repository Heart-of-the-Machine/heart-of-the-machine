package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

class ServerTowerFeature(codec: Codec<ServerTowerConfig>) : Feature<ServerTowerConfig>(codec) {
    override fun generate(
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: ServerTowerConfig
    ): Boolean {
        val height = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight
        val size = random.nextInt(config.maxSize - config.minSize + 1) + config.minSize
        val border = (random.nextInt(config.maxBorder - config.minBorder + 1) + config.minBorder).coerceAtMost(size - size / 2 - 1)
            .coerceAtLeast(0)
        val windowSize = size - border * 2

        return if (FeatureUtils.isFilledWithAir(
                world,
                pos.x - size + size / 2 + 1,
                pos.y + 1,
                pos.z - size + size / 2 + 1,
                pos.x + size / 2,
                pos.y + height + size + 1,
                pos.z + size / 2
            )
        ) {
            buildBase(world, pos, size, config)

            FeatureUtils.fillBlocks(
                world,
                pos.x - size + size / 2 + 1,
                pos.y,
                pos.z - size + size / 2 + 1,
                pos.x + size / 2,
                pos.y + height - 1,
                pos.z + size / 2,
                config.structure
            )

            FeatureUtils.fillBlocks(
                world,
                pos.x - size + size / 2 + 1,
                pos.y + height,
                pos.z - size + size / 2 + 1,
                pos.x + size / 2,
                pos.y + height,
                pos.z + size / 2,
                if (random.nextFloat() < config.flatLampChance) {
                    config.flatLamp
                } else {
                    config.structure
                }
            )

            FeatureUtils.fillBlocks(
                world,
                pos.x - size + size / 2 + 1,
                pos.y + height + 1,
                pos.z - size + size / 2 + 1,
                pos.x + size / 2,
                pos.y + height + size,
                pos.z + size / 2,
                config.structure
            )

            // west window
            FeatureUtils.fillBlocks(
                world,
                pos.x - size + size / 2 + 1,
                pos.y + height + border + 1,
                pos.z - windowSize + windowSize / 2 + 1,
                pos.x - size + size / 2 + 1,
                pos.y + height + size - border,
                pos.z + windowSize / 2,
                config.lamp
            )

            // east window
            FeatureUtils.fillBlocks(
                world,
                pos.x + size / 2,
                pos.y + height + border + 1,
                pos.z - windowSize + windowSize / 2 + 1,
                pos.x + size / 2,
                pos.y + height + size - border,
                pos.z + windowSize / 2,
                config.lamp
            )

            // north window
            FeatureUtils.fillBlocks(
                world,
                pos.x - windowSize + windowSize / 2 + 1,
                pos.y + height + border + 1,
                pos.z - size + size / 2 + 1,
                pos.x + windowSize / 2,
                pos.y + height + size - border,
                pos.z - size + size / 2 + 1,
                config.lamp
            )

            // south window
            FeatureUtils.fillBlocks(
                world,
                pos.x - windowSize + windowSize / 2 + 1,
                pos.y + height + border + 1,
                pos.z + size / 2,
                pos.x + windowSize / 2,
                pos.y + height + size - border,
                pos.z + size / 2,
                config.lamp
            )

            FeatureUtils.fillBlocks(
                world,
                pos.x - size + size / 2 + 1,
                pos.y + height + size + 1,
                pos.z - size + size / 2 + 1,
                pos.x + size / 2,
                pos.y + height + size + 1,
                pos.z + size / 2,
                if (random.nextFloat() < config.flatLampChance) {
                    config.flatLamp
                } else {
                    config.structure
                }
            )

            true
        } else {
            false
        }
    }

    private fun buildBase(world: ServerWorldAccess, pos: BlockPos, size: Int, config: ServerTowerConfig) {
        val minY = findLowest(world, pos, size, config)
        FeatureUtils.fillBlocks(
            world,
            pos.x - size + size / 2 + 1,
            minY,
            pos.z - size + size / 2 + 1,
            pos.x + size / 2,
            pos.y,
            pos.z + size / 2,
            config.structure
        )
    }

    private fun findLowest(world: ServerWorldAccess, pos: BlockPos, size: Int, config: ServerTowerConfig): Int {
        val mutable = pos.mutableCopy()
        var y = pos.y
        var water = false

        while (y >= (pos.y - config.maxDrop) || water) {
            water = false
            var solid = true

            for (x in (pos.x - size + size / 2 + 1)..(pos.x + size / 2)) {
                for (z in (pos.z - size + size / 2 + 1)..(pos.z + size / 2)) {
                    mutable.set(x, y, z)
                    if (world.isAir(mutable) || world.isWater(mutable)) {
                        solid = false
                    }
                    if (world.isWater(mutable)) {
                        water = true
                    }
                }
            }

            if (solid) {
                return y + 1
            }

            y--
        }

        return config.maxDrop
    }
}