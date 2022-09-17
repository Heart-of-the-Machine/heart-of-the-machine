package com.github.hotm.items

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Direction

class BracingItem(block: Block, settings: Settings) : BlockItem(block, settings) {
    companion object {
        const val BRACING_DISTANCE = 16
    }

    override fun getPlacementContext(context: ItemPlacementContext): ItemPlacementContext? {
        val blockPos = context.blockPos.offset(context.side.opposite)
        val world = context.world
        var blockState = world.getBlockState(blockPos)
        val block = block

        return if (!blockState.isOf(block)) {
            context
        } else {
            val placementDir = if (context.side == Direction.UP) context.playerFacing else Direction.UP

            var i = 0
            val mutable = blockPos.mutableCopy().move(placementDir)

            while (i < BRACING_DISTANCE) {
                if (!world.isClient && !world.isInBuildLimit(mutable)) {
                    val playerEntity = context.player
                    val j = world.height
                    if (playerEntity is ServerPlayerEntity && mutable.y >= j) {
                        playerEntity.sendMessage(
                            Text.translatable("build.tooHigh", j - 1).formatted(Formatting.RED),
                            true
                        )
                    }
                    break
                }

                blockState = world.getBlockState(mutable)
                if (!blockState.isOf(this.block)) {
                    if (blockState.canReplace(context)) {
                        return ItemPlacementContext.offset(context, mutable, placementDir)
                    }
                    break
                }

                mutable.move(placementDir)

                if (placementDir.axis.isHorizontal) {
                    ++i
                }
            }
            null
        }
    }

    override fun checkStatePlacement(): Boolean {
        return false
    }
}
