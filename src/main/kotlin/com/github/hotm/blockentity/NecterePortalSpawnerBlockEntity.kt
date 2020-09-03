package com.github.hotm.blockentity

import com.github.hotm.HotMBlockEntities
import com.github.hotm.HotMBlocks
import com.github.hotm.gen.feature.NecterePortalGen
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import net.minecraft.util.math.ChunkPos
import java.util.*

class NecterePortalSpawnerBlockEntity : BlockEntity(HotMBlockEntities.NECTERE_PORTAL_SPAWNER_BLOCK_ENTITY), Tickable {
    var originalBlock = Blocks.AIR.defaultState

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        originalBlock =
            BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("original")).result().orElse(Blocks.AIR.defaultState)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val newTag = super.toTag(tag)

        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalBlock).result().ifPresent { encoded ->
            newTag.put("original", encoded)
        }

        return newTag
    }

    override fun tick() {
        val world = world
        if (world != null && world is ServerWorld) {
            world.removeBlockEntity(pos)

            if (originalBlock.block != HotMBlocks.NECTERE_PORTAL_SPAWNER) {
                world.setBlockState(pos, originalBlock)
            }

            spawn()
        }
    }

    fun spawn() {
        val world = world
        if (world != null && world is ServerWorld) {
            val random = Random()
            NecterePortalGen.generateForChunk(world, ChunkPos(pos), random)
        }
    }
}