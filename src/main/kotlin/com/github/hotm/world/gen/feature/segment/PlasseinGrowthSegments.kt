package com.github.hotm.world.gen.feature.segment

import com.github.hotm.util.CardinalDirection
import com.github.hotm.world.gen.feature.FeatureUtils
import com.github.hotm.world.gen.feature.segment.FeatureSegmentUtils.tryFill
import com.github.hotm.world.gen.feature.segment.FeatureSegmentUtils.tryPlace
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.terraformersmc.terraform.shapes.api.Position
import com.terraformersmc.terraform.shapes.impl.Shapes
import com.terraformersmc.terraform.shapes.impl.layer.pathfinder.SubtractLayer
import com.terraformersmc.terraform.shapes.impl.layer.transform.TranslateLayer
import net.minecraft.block.BlockState
import net.minecraft.block.PillarBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

/**
 * Generates a plassein stem.
 */
class PlasseinStemSegment(
    val stalk: BlockState,
    val heightMin: Int,
    val heightVariation: Int,
    val branchSegment: FeatureSegment<CardinalDirection>,
    val leafSegment: FeatureSegment<Unit>
) : FeatureSegment<Unit> {
    companion object {
        val CODEC: Codec<PlasseinStemSegment> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PlasseinStemSegment> ->
                instance.group(
                    BlockState.CODEC.fieldOf("stalk").forGetter { it.stalk },
                    Codec.INT.fieldOf("height_min").forGetter { it.heightMin },
                    Codec.INT.fieldOf("height_variation").forGetter { it.heightVariation },
                    FeatureSegment.CARDINAL_CODEC.fieldOf("branch_segment").forGetter { it.branchSegment },
                    FeatureSegment.UNIT_CODEC.fieldOf("leaf_segment").forGetter { it.leafSegment }
                ).apply(instance) { stalk, heightMin, heightVariation, branchSegment, leafSegment ->
                    PlasseinStemSegment(stalk, heightMin, heightVariation, branchSegment, leafSegment)
                }
            }
    }

    override val type = HotMFeatureSegmentTypes.PLASSEIN_STEM_FEATURE_SEGMENT

    override fun tryGenerate(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        children: MutableCollection<PositionedFeatureSegment<*>>,
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        context: Unit
    ): Boolean {
        if (!world.testBlockState(pos.down(), FeatureUtils::isSurface)) {
            return false
        }

        val height = random.nextInt(heightVariation + 1) + heightMin
        val split = height / 2

        val up = pos.up(height)
        val north = pos.north()
        val northUp = north.up(split)
        val south = pos.south()
        val southUp = south.up(split)
        val west = pos.west()
        val westUp = west.up(split)
        val east = pos.east()
        val eastUp = east.up(split)

        if (!tryFill(blocks, world, pos, up, BlockPlacement(stalk, false, 19, 0, LeafPlacement.SOURCE))
            || !tryFill(blocks, world, north, northUp, BlockPlacement(stalk, false, 19, 0, LeafPlacement.SOURCE))
            || !tryFill(blocks, world, south, southUp, BlockPlacement(stalk, false, 19, 0, LeafPlacement.SOURCE))
            || !tryFill(blocks, world, west, westUp, BlockPlacement(stalk, false, 19, 0, LeafPlacement.SOURCE))
            || !tryFill(blocks, world, east, eastUp, BlockPlacement(stalk, false, 19, 0, LeafPlacement.SOURCE))
        ) {
            return false
        }

        children.add(PositionedFeatureSegment(northUp.up(), branchSegment, CardinalDirection.NORTH))
        children.add(PositionedFeatureSegment(southUp.up(), branchSegment, CardinalDirection.SOUTH))
        children.add(PositionedFeatureSegment(eastUp.up(), branchSegment, CardinalDirection.EAST))
        children.add(PositionedFeatureSegment(westUp.up(), branchSegment, CardinalDirection.WEST))
        children.add(PositionedFeatureSegment(up.up(), leafSegment, Unit))

        return true
    }
}

/**
 * Generates a plassein branch.
 */
class PlasseinBranchSegment(
    val branch: BlockState,
    val heightMin: Int,
    val heightVariation: Int,
    val leafSegment: FeatureSegment<Unit>
) : FeatureSegment<CardinalDirection> {
    companion object {
        val CODEC: Codec<PlasseinBranchSegment> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PlasseinBranchSegment> ->
                instance.group(
                    BlockState.CODEC.fieldOf("branch").forGetter { it.branch },
                    Codec.INT.fieldOf("height_min").forGetter { it.heightMin },
                    Codec.INT.fieldOf("height_variation").forGetter { it.heightVariation },
                    FeatureSegment.UNIT_CODEC.fieldOf("leaf_segment").forGetter { it.leafSegment }
                ).apply(instance) { branch, heightMin, heightVariation, leafSegment ->
                    PlasseinBranchSegment(
                        branch,
                        heightMin,
                        heightVariation,
                        leafSegment
                    )
                }
            }
    }

    override val type = HotMFeatureSegmentTypes.PLASSEIN_BRANCH_FEATURE_SEGMENT

    override fun tryGenerate(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        children: MutableCollection<PositionedFeatureSegment<*>>,
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        context: CardinalDirection
    ): Boolean {
        val height = random.nextInt(heightVariation + 1) + heightMin
        val corner = pos.offset(Direction.UP, height)
        val end = corner.offset(context.direction, height)
        val minX = min(pos.x, end.x)
        val minZ = min(pos.z, end.z)
        val maxX = max(pos.x, end.x)
        val maxZ = max(pos.z, end.z)

        val mutable = BlockPos.Mutable()
        for (y in pos.y..end.y) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    mutable.set(x, y, z)
                    if (mutable.isWithinDistance(corner, height.toDouble() + 1)
                        && !mutable.isWithinDistance(corner, height.toDouble())
                    ) {
                        val offsetX = x - corner.x
                        val offsetY = y - corner.y
                        val offsetZ = z - corner.z

                        val branchDirection = if (offsetX * offsetX + offsetZ * offsetZ > offsetY * offsetY) {
                            Direction.UP
                        } else {
                            context.direction
                        }

                        val state = branch.with(PillarBlock.AXIS, branchDirection.axis)

                        if (!tryPlace(
                                blocks,
                                world,
                                mutable,
                                BlockPlacement(state, false, 19, 0, LeafPlacement.SOURCE)
                            )
                        ) {
                            return false
                        }
                    }
                }
            }
        }

        children.add(PositionedFeatureSegment(end.up(), leafSegment, Unit))

        return true
    }
}

class PlasseinLeafSegment(
    val leaf: BlockState,
    val radiusMin: Int,
    val radiusVariation: Int,
    val depthMin: Int,
    val depthVariation: Int
) : FeatureSegment<Unit> {
    companion object {
        val CODEC: Codec<PlasseinLeafSegment> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PlasseinLeafSegment> ->
                instance.group(
                    BlockState.CODEC.fieldOf("leaf").forGetter { it.leaf },
                    Codec.INT.fieldOf("radius_min").forGetter { it.radiusMin },
                    Codec.INT.fieldOf("radius_variation").forGetter { it.radiusVariation },
                    Codec.INT.fieldOf("depth_min").forGetter { it.depthMin },
                    Codec.INT.fieldOf("depth_variation").forGetter { it.depthVariation }
                ).apply(instance) { leaf, radiusMin, radiusVariation, depthMin, depthVariation ->
                    PlasseinLeafSegment(
                        leaf,
                        radiusMin,
                        radiusVariation,
                        depthMin,
                        depthVariation
                    )
                }
            }
    }

    override val type = HotMFeatureSegmentTypes.PLASSEIN_LEAF_FEATURE_SEGMENT

    override fun tryGenerate(
        blocks: MutableMap<BlockPos, BlockPlacement>,
        children: MutableCollection<PositionedFeatureSegment<*>>,
        world: StructureWorldAccess,
        generator: ChunkGenerator,
        random: Random,
        pos: BlockPos,
        context: Unit
    ): Boolean {
        val radius = random.nextInt(radiusVariation + 1) + radiusMin
        val depth = random.nextInt(depthVariation + 1) + depthMin

        // `applyLayer` seems to mutate the underlying shape so we need separate shapes here
        val upper = Shapes.ellipsoid(radius.toDouble(), radius.toDouble(), radius.toDouble() / 2)
            .applyLayer(TranslateLayer.of(Position.of(pos.down(radius / 2 - (depth - depth / 2)))))
        val lower = Shapes.ellipsoid(radius.toDouble(), radius.toDouble(), radius.toDouble() / 2)
            .applyLayer(TranslateLayer.of(Position.of(pos.down(radius / 2 + depth / 2))))
        val bloom = upper.applyLayer(SubtractLayer(lower))
        bloom.fill(SegmentFiller(blocks, BlockPlacement(leaf, false, 19, 10, LeafPlacement.LEAF)))

        return true
    }
}
