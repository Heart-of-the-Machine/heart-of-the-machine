package com.github.hotm.blocks

import com.github.hotm.blockentity.BasicSiphonAuraNodeBlockEntity
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.BasicSiphonAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos

class BasicSiphonAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlockWithEntity(settings) {
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
}