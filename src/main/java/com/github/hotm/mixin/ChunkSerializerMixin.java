package com.github.hotm.mixin;

import com.github.hotm.mixinapi.StorageUtils;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Used to allow the meta block system to be able to handle network blocks being added outside of a game.
 */
@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "deserialize", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/poi/PointOfInterestStorage;initForPalette(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/ChunkSection;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onDeserialize(ServerWorld world, StructureManager structureManager,
                                      PointOfInterestStorage poiStorage, ChunkPos pos, NbtCompound nbt,
                                      CallbackInfoReturnable<ProtoChunk> cir, ChunkGenerator chunkGenerator,
                                      BiomeSource biomeSource, NbtCompound nbtCompound, BiomeArray biomeArray,
                                      UpgradeData upgradeData, ChunkTickScheduler<Block> chunkTickScheduler,
                                      ChunkTickScheduler<Fluid> chunkTickScheduler2, boolean bl, NbtList nbtList, int i,
                                      ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager,
                                      LightingProvider lightingProvider, int j, NbtCompound nbtCompound2, int k,
                                      ChunkSection chunkSection) {
        StorageUtils.handleDeserialize(pos, chunkSection);
    }
}
