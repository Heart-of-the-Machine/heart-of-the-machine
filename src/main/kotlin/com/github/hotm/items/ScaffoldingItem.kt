package com.github.hotm.items

import com.github.hotm.blocks.ScaffoldingBlock
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.network.MessageType
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ScaffoldingItem(block: Block, settings: Settings) : BlockItem(block, settings) {
    override fun getPlacementContext(context: ItemPlacementContext): ItemPlacementContext? {
        val blockPos = context.blockPos
        val world = context.world
        var blockState = world.getBlockState(blockPos)
        val block = block

        return if (!blockState.isOf(block)) {
            if (ScaffoldingBlock.calculateDistance(world, blockPos) == 7) null else context
        } else {
            val direction2: Direction = if (context.shouldCancelInteraction()) {
                if (context.hitsInsideBlock()) context.side.opposite else context.side
            } else {
                if (context.side == Direction.UP) context.playerFacing else Direction.UP
            }

            var i = 0
            val mutable = blockPos.mutableCopy().move(direction2)
            while (i < 7) {
                if (!world.isClient && !World.method_24794(mutable)) {
                    val playerEntity = context.player
                    val j = world.height
                    if (playerEntity is ServerPlayerEntity && mutable.y >= j) {
                        val gameMessageS2CPacket = GameMessageS2CPacket(
                            TranslatableText("build.tooHigh", j).formatted(
                                Formatting.RED
                            ), MessageType.GAME_INFO, Util.NIL_UUID
                        )
                        playerEntity.networkHandler.sendPacket(gameMessageS2CPacket)
                    }
                    break
                }

                blockState = world.getBlockState(mutable)
                if (!blockState.isOf(this.block)) {
                    if (blockState.canReplace(context)) {
                        return ItemPlacementContext.offset(context, mutable, direction2)
                    }
                    break
                }

                mutable.move(direction2)

                if (direction2.axis.isHorizontal) {
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