package com.github.hotm.mixin;

import com.github.hotm.gen.HotMDimensions;
import com.github.hotm.gen.feature.HotMStructureFeatures;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin used to get the custom Nectere portal /locate logic to work.
 */
@SuppressWarnings("unused")
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(method = "locateStructure", at = @At("HEAD"), cancellable = true)
    private void onLocateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius,
                                   boolean skipExistingChunks, CallbackInfoReturnable<BlockPos> cir) {
        if (!world.getRegistryKey().equals(HotMDimensions.INSTANCE.getNECTERE_KEY()) &&
                feature == HotMStructureFeatures.INSTANCE.getNECTERE_PORTAL()) {
            cir.setReturnValue(
                    HotMDimensions.INSTANCE.locateNonNectereSidePortal(world, center, radius, skipExistingChunks));
        }
    }
}
