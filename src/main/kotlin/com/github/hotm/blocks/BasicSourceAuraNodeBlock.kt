package com.github.hotm.blocks

import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.AuraNodeType
import com.github.hotm.world.auranet.BasicSourceAuraNode
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos

class BasicSourceAuraNodeBlock(settings: Settings) : AbstractAuraNodeBlock(settings) {
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
}