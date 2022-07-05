package com.github.hotm.blockentity

import com.github.hotm.blocks.HotMBlocks
import com.github.hotm.world.gen.HotMPortalGen
import com.github.hotm.world.gen.feature.HotMStructureFeatures
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
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
    var structureCtx: StructureContext? = null

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)

        originalBlock =
            BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("original")).result().orElse(Blocks.AIR.defaultState)

        if (tag.contains("structure")) {
            val struct = tag.getCompound("structure")

            structureCtx = StructureContext(
                BlockBox.CODEC.parse(NbtOps.INSTANCE, struct.get("boundingBox")).result()
                    .orElse(BlockBox(pos.x, 64, pos.z, pos.x + 5, 68, pos.z + 5)),
                if (struct.contains("facing")) {
                    Direction.byId(struct.getByte("facing").toInt())
                } else null,
                BlockMirror.values()[struct.getByte("mirror").toInt().coerceIn(0, 2)],
                BlockRotation.values()[struct.getByte("rotation").toInt().coerceIn(0, 3)]
            )
        }
    }

    override fun writeNbt(tag: NbtCompound) {
        super.writeNbt(tag)

        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalBlock).result().ifPresent { encoded ->
            tag.put("original", encoded)
        }

        val structureCtx = structureCtx
        if (structureCtx != null) {
            val struct = NbtCompound()
            BlockBox.CODEC.encodeStart(NbtOps.INSTANCE, structureCtx.boundingBox).result().ifPresent { encoded ->
                struct.put("boundingBox", encoded)
            }
            if (structureCtx.facing != null) {
                struct.putByte("facing", structureCtx.facing.id.toByte())
            }
            struct.putByte("mirror", structureCtx.mirror.ordinal.toByte())
            struct.putByte("rotation", structureCtx.rotation.ordinal.toByte())
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
            val tag = NbtCompound()
            writeNbt(tag)
            world.getChunk(pos).addPendingBlockEntityNbt(tag)

            world.removeBlockEntity(pos)

            if (originalBlock.block != HotMBlocks.NECTERE_PORTAL_SPAWNER) {
                world.setBlockState(pos, originalBlock)
            }

            if (world.structureAccessor.shouldGenerateStructures()) {
                val structureCtx = structureCtx
                if (structureCtx == null) {
                    // We're generating as a feature, so we just check if there are any portals connected and generate
                    // them if so.
                    HotMPortalGen.generateNonNectereSideForChunk(world, ChunkPos(pos))
                } else {
                    // We're generating as a structure, so we generate the portal in the place of the structure or
                    // remove the structure if we couldn't generate the portal.
                    if (!HotMPortalGen.generateNectereSideForStructure(
                            world,
                            structureCtx.boundingBox,
                            structureCtx::applyXTransform,
                            structureCtx::applyYTransform,
                            structureCtx::applyZTransform,
                            structureCtx.mirror,
                            structureCtx.rotation
                        )
                    ) {
                        // The portal structure couldn't be generated here so we'll remove the structure piece from the
                        // structure start.
                        world.structureAccessor.getStructureAt(
                            structureCtx.boundingBox.center,
                            HotMStructureFeatures.NECTERE_PORTAL
                        ).clearChildren()
                    }
                }
            }
        }
    }

    data class StructureContext(
        val boundingBox: BlockBox,
        val facing: Direction?,
        val mirror: BlockMirror,
        val rotation: BlockRotation
    ) {
        fun applyXTransform(x: Int, z: Int): Int {
            return if (facing == null) {
                x
            } else {
                when (facing) {
                    Direction.NORTH, Direction.SOUTH -> boundingBox.minX + x
                    Direction.WEST -> boundingBox.maxX - z
                    Direction.EAST -> boundingBox.minX + z
                    else -> x
                }
            }
        }

        fun applyYTransform(y: Int): Int {
            return if (facing == null) y else y + boundingBox.minY
        }

        fun applyZTransform(x: Int, z: Int): Int {
            return if (facing == null) {
                z
            } else {
                when (facing) {
                    Direction.NORTH -> boundingBox.maxZ - z
                    Direction.SOUTH -> boundingBox.minZ + z
                    Direction.WEST, Direction.EAST -> boundingBox.minZ + x
                    else -> z
                }
            }
        }
    }
}
