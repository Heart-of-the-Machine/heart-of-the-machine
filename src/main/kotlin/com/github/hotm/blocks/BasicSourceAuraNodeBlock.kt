package com.github.hotm.blocks

import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.BasicSourceAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class BasicSourceAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlock(settings) {
    companion object {
        private val SHAPE = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
    }

    override val auraNodeType: AuraNodeType<out AuraNode>
        get() = BasicSourceAuraNode.Type

    override fun createAuraNode(
        state: BlockState,
        world: ServerWorld,
        storage: ServerAuraNetStorage,
        pos: BlockPos
    ): AuraNode {
        return BasicSourceAuraNode(storage, storage.getUpdateListener(ChunkSectionPos.from(pos)), pos, 0, listOf())
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }
}