package com.github.hotm.mod.blockentity

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.world.gen.HotMPortalGen
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World

/**
 * Used for generating Nectere portals on the main server thread where it's ok to access other dimensions so we can make
 * sure the portals are placed right.
 */
class NecterePortalSpawnerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HotMBlockEntities.NECTERE_PORTAL_SPAWNER, pos, state) {
    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, entity: NecterePortalSpawnerBlockEntity) {
            entity.tick()
        }
    }

    var originalBlock: BlockState = Blocks.AIR.defaultState

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)

        originalBlock =
            BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("original")).result().orElse(Blocks.AIR.defaultState)
    }

    override fun writeNbt(tag: NbtCompound) {
        super.writeNbt(tag)

        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalBlock).result().ifPresent { encoded ->
            tag.put("original", encoded)
        }
    }

    fun tick() {
        val world = world
        if (world != null && !world.isClient) {
            spawn()
        }
    }

    fun spawn() {
        val world = world
        if (world != null && world is ServerWorld) {
            // This is an awful hack to get minecraft to stop complaining about these BlockEntities being removed but
            // still pending.
            world.getChunk(pos).setBlockEntityNbt(toIdentifiedLocatedNbt())

            world.removeBlockEntity(pos)

            if (originalBlock.block != HotMBlocks.NECTERE_PORTAL_SPAWNER) {
                world.setBlockState(pos, originalBlock)
            }

            if (world.structureManager.shouldGenerate()) {
                // We're generating as a feature, so we just check if there are any portals connected and generate
                // them if so.
                HotMPortalGen.generateNonNectereSideForChunk(world, ChunkPos(pos))
            }
        }
    }
}
