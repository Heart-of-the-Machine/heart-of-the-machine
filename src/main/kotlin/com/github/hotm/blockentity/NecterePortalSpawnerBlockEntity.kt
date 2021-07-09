package com.github.hotm.blockentity

import com.github.hotm.HotMBlockEntities
import com.github.hotm.HotMBlocks
import com.github.hotm.world.gen.feature.NecterePortalGen
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import java.util.*

class NecterePortalSpawnerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HotMBlockEntities.NECTERE_PORTAL_SPAWNER_BLOCK_ENTITY, pos, state) {
    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, entity: NecterePortalSpawnerBlockEntity) {
            entity.tick()
        }
    }

    var originalBlock = Blocks.AIR.defaultState

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)

        originalBlock =
            BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("original")).result().orElse(Blocks.AIR.defaultState)
    }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        val newTag = super.writeNbt(tag)

        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalBlock).result().ifPresent { encoded ->
            newTag.put("original", encoded)
        }

        return newTag
    }

    fun tick() {
        val world = world
        if (world != null && world is ServerWorld) {
            // This is an awful hack to get minecraft to stop complaining about these BlockEntities being removed but
            // still pending.
            world.getChunk(pos).addPendingBlockEntityNbt(writeNbt(NbtCompound()))

            world.removeBlockEntity(pos)

            if (originalBlock.block != HotMBlocks.NECTERE_PORTAL_SPAWNER) {
                world.setBlockState(pos, originalBlock)
            }

            spawn()
        }
    }

    fun spawn() {
        val world = world
        if (world != null && world is ServerWorld && world.structureAccessor.shouldGenerateStructures()) {
            val random = Random()
            NecterePortalGen.generateForChunk(world, ChunkPos(pos), random)
        }
    }
}