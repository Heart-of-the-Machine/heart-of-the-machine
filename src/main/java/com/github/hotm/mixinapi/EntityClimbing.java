package com.github.hotm.mixinapi;

import com.github.hotm.blocks.BracingBlock;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;
import java.util.function.Function;

public class EntityClimbing {
    private static final List<Function<BlockPos, BlockPos>> OFFSET_FUNCTIONS = ImmutableList
            .of((pos) -> pos.offset(Direction.NORTH).offset(Direction.WEST), (pos) -> pos.offset(Direction.NORTH),
                    (pos) -> pos.offset(Direction.NORTH).offset(Direction.EAST), (pos) -> pos.offset(Direction.EAST),
                    (pos) -> pos.offset(Direction.SOUTH).offset(Direction.EAST), (pos) -> pos.offset(Direction.SOUTH),
                    (pos) -> pos.offset(Direction.SOUTH).offset(Direction.WEST), (pos) -> pos.offset(Direction.WEST));

    public static boolean isClimbing(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();

        for (Function<BlockPos, BlockPos> offsetFn : OFFSET_FUNCTIONS) {
            BlockPos dirPos = offsetFn.apply(pos);
            BlockState state = entity.world.getBlockState(dirPos);

            if (state.getBlock() instanceof BracingBlock && VoxelShapes.matchesAnywhere(VoxelShapes
                            .cuboid(entity.getBoundingBox().offset(-dirPos.getX(), -dirPos.getY(), -dirPos.getZ())),
                    state.getOutlineShape(entity.world, dirPos), BooleanBiFunction.AND)) {
                return true;
            }
        }

        return false;
    }
}
