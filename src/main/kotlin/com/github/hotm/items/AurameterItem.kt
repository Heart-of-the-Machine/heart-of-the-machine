package com.github.hotm.items

import com.github.hotm.HotMConstants.message
import com.github.hotm.auranet.ValuedAuraNode
import com.github.hotm.mixinapi.StorageUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World

class AurameterItem(settings: Settings) : Item(settings), InteractionCanceler {
    override fun shouldCancelInteraction(usageContext: ItemUsageContext): Boolean {
        return true
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val user = context.player
        return if (user == null) {
            ActionResult.FAIL
        } else {
            val world = context.world
            if (user.isSneaking) {
                if (world.isClient) {
                    ActionResult.SUCCESS
                } else {
                    printChunkAura(world as ServerWorld, user)
                    ActionResult.CONSUME
                }
            } else {
                val pos = context.blockPos
                val access = StorageUtils.getAuraNetAccess(world)
                val node = access[pos] as? ValuedAuraNode

                if (node == null) {
                    ActionResult.FAIL
                } else {
                    if (world.isClient) {
                        ActionResult.SUCCESS
                    } else {
                        user.sendMessage(message("aurameter.node", node.getValue()), false)
                        ActionResult.CONSUME
                    }
                }
            }
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return if (user.isSneaking) {
            if (world.isClient) {
                TypedActionResult.success(user.getStackInHand(hand))
            } else {
                printChunkAura(world as ServerWorld, user)
                TypedActionResult.consume(user.getStackInHand(hand))
            }
        } else {
            TypedActionResult.fail(user.getStackInHand(hand))
        }
    }

    private fun printChunkAura(world: ServerWorld, player: PlayerEntity) {
        val storage = StorageUtils.getServerAuraNetStorage(world)
        val sectionPos = ChunkSectionPos.from(player.blockPos)
        val base = storage.getBaseAura(sectionPos)
        val total = storage.getSectionAura(sectionPos)

        player.sendMessage(message("aurameter.chunk", base, total), false)
    }
}