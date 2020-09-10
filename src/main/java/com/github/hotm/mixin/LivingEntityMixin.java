package com.github.hotm.mixin;

import com.github.hotm.blocks.BracingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void onIsClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isSpectator()) {
            BlockPos pos = self.getBlockPos();

            for (int i = 0; i < 4; i++) {
                Direction dir = Direction.fromHorizontal(i);
                BlockPos dirPos = pos.offset(dir);
                BlockState state = self.world.getBlockState(dirPos);

                if (state.getBlock() instanceof BracingBlock && VoxelShapes.matchesAnywhere(VoxelShapes
                                .cuboid(self.getBoundingBox().offset(-dirPos.getX(), -dirPos.getY(), -dirPos.getZ())),
                        state.getOutlineShape(self.world, dirPos), BooleanBiFunction.AND)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
