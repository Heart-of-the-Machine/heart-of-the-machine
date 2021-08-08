package com.github.hotm.blocks

import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.particle.HotMParticles
import com.github.hotm.meta.MetaBlock
import com.github.hotm.meta.MetaBlockType
import com.github.hotm.meta.auranet.BasicSourceAuraNode
import com.github.hotm.world.meta.server.ServerMetaStorage
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class BasicSourceAuraNodeBlock(settings: Settings) : AbstractBlockWithMeta(settings) {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override val metaBlockType: MetaBlockType<out MetaBlock>
        get() = BasicSourceAuraNode.Type

    override fun createMetaBlock(
        state: BlockState,
        world: ServerWorld,
        storage: ServerMetaStorage,
        pos: BlockPos
    ): MetaBlock {
        return BasicSourceAuraNode(storage, storage.getUpdateListener(ChunkSectionPos.from(pos)), pos, 0f, listOf())
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val access = StorageUtils.getMetaAccess(world)
        val node = access[pos]

        if (node != null && node.getValue() > 0) {
            val x = pos.x.toDouble() + 0.45 + random.nextDouble() * 0.1
            val y = pos.y.toDouble() + 0.45 + random.nextDouble() * 0.1
            val z = pos.z.toDouble() + 0.45 + random.nextDouble() * 0.1
            if (random.nextInt(3) == 0) {
                world.addParticle(
                    HotMParticles.AURA_SOURCE,
                    x,
                    y,
                    z,
                    random.nextDouble() * 2.0 - 1.0,
                    random.nextDouble() * 2.0 - 1.0,
                    random.nextDouble() * 2.0 - 1.0
                )
            }
        }
    }
}