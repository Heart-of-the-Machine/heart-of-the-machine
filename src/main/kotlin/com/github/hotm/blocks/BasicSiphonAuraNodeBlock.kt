package com.github.hotm.blocks

import com.github.hotm.blockentity.AbstractDependableAuraNodeBlockEntity
import com.github.hotm.blockentity.BasicSiphonAuraNodeBlockEntity
import com.github.hotm.blockentity.HotMBlockEntities
import com.github.hotm.particle.HotMParticles
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.BasicSiphonAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class BasicSiphonAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlockWithEntity(settings) {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override val auraNodeType: AuraNodeType<out AuraNode>
        get() = BasicSiphonAuraNode.Type

    override fun createAuraNode(
        state: BlockState,
        world: ServerWorld,
        storage: ServerAuraNetStorage,
        pos: BlockPos
    ): AuraNode {
        return BasicSiphonAuraNode(storage, storage.getUpdateListener(ChunkSectionPos.from(pos)), pos, 0, null)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BasicSiphonAuraNodeBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type,
            HotMBlockEntities.BASIC_SIPHON_AURA_NODE,
            if (world.isClient) null else AbstractDependableAuraNodeBlockEntity.Companion::tickServer
        )
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val x = pos.x.toDouble() + 0.45 + random.nextDouble() * 0.1
        val y = pos.y.toDouble() + 0.45 + random.nextDouble() * 0.1
        val z = pos.z.toDouble() + 0.45 + random.nextDouble() * 0.1
        if (random.nextInt(3) == 0) {
            world.addParticle(
                HotMParticles.AURA_SIPHON,
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