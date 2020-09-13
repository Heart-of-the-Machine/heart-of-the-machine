package com.github.hotm.gen.feature

import com.mojang.serialization.Codec
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import java.util.*

class TransmissionTowerFeature(codec: Codec<TransmissionTowerConfig>) : Feature<TransmissionTowerConfig>(codec) {
    override fun generate(
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        config: TransmissionTowerConfig
    ): Boolean {
        val baseList = mutableListOf<BlockPos>()
        val structureList = mutableListOf<BlockPos>()
        val structureGrowthList = mutableListOf<BlockPos>()
        val leafList = mutableListOf<BlockPos>()
        val lampList = mutableListOf<BlockPos>()

        return if (tryGenerate(
                world,
                random,
                pos,
                config,
                baseList,
                structureList,
                structureGrowthList,
                leafList,
                lampList
            )
        ) {
            for (basePos in baseList) {
                dropDown(world, basePos, config.maxDrop, config.structure)
            }

            for (structurePos in structureList) {
                world.setBlockState(structurePos, config.structure, 3)
            }

            for (structureGrowthPos in structureGrowthList) {
                world.setBlockState(structureGrowthPos, config.structureGrowth, 3)
            }

            for (leafPos in leafList) {
                world.setBlockState(leafPos, config.leaf, 19)
            }

            for (lampPos in lampList) {
                world.setBlockState(lampPos, config.lamp, 3)
            }

            true
        } else {
            false
        }
    }

    private fun tryGenerate(
        world: ServerWorldAccess,
        random: Random,
        pos: BlockPos,
        config: TransmissionTowerConfig,
        baseList: MutableCollection<BlockPos>,
        structureList: MutableCollection<BlockPos>,
        structureGrowthList: MutableCollection<BlockPos>,
        leafList: MutableCollection<BlockPos>,
        lampList: MutableCollection<BlockPos>
    ): Boolean {
        if (!world.testBlockState(pos.down()) { FeatureUtils.isSurface(it.block) }) {
            return false
        }

        val height = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight

        val heights = Object2IntOpenHashMap<BlockPos>()
        val positionQueue = ArrayDeque<BlockPosHeight>()
        positionQueue.add(BlockPosHeight(pos, height))

        // spread out and calculate heights
        while (!positionQueue.isEmpty()) {
            val cur = positionQueue.remove()
            heights[cur.pos] = cur.height
            for (i in 0 until 4) {
                val child = cur.pos.offset(Direction.fromHorizontal(i))
                if (!heights.containsKey(child) && world.isAir(child)) {
                    val falloff = random.nextInt(config.maxFalloff - config.minFalloff + 1) + config.minFalloff
                    if (cur.height > falloff) {
                        positionQueue.add(BlockPosHeight(child, cur.height - falloff))
                    }
                }
            }
        }

        baseList.addAll(heights.keys)

        for (e in heights.object2IntEntrySet()) {
            val curPos = e.key
            val mutable = curPos.mutableCopy()
            val curHeight = e.intValue
            for (y in curPos.y until (curPos.y + curHeight)) {
                mutable.y = y

                if (!world.isAir(mutable)) {
                    return false
                }

                if (random.nextFloat() < config.growthChance) {
                    structureGrowthList.add(mutable.toImmutable())

                    for (i in 0 until 4) {
                        if (random.nextFloat() < config.leafChance) {
                            val baseOffset = curPos.offset(Direction.fromHorizontal(i))
                            val offset = mutable.offset(Direction.fromHorizontal(i))
                            if (world.isAir(offset)
                                && heights.getOrDefault(baseOffset as Any, 0) <= offset.y - baseOffset.y
                            ) {
                                leafList.add(offset)
                            }
                        }
                    }
                } else {
                    structureList.add(mutable.toImmutable())
                }
            }
        }

        lampList.add(pos.offset(Direction.UP, heights.getInt(pos)))

        return true
    }

    private fun dropDown(world: ServerWorldAccess, pos: BlockPos, max: Int, structure: BlockState) {
        val mutable = pos.mutableCopy()
        for (y in (pos.y - 1) downTo (pos.y - max)) {
            mutable.y = y
            if (world.isAir(mutable)) {
                world.setBlockState(mutable, structure, 3)
            }
        }
    }

    private data class BlockPosHeight(val pos: BlockPos, val height: Int)
}