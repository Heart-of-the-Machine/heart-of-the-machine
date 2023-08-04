package com.github.hotm.mod.item

import com.github.hotm.mod.Constants.msg
import com.github.hotm.mod.auranet.ValuedAuraNode
import com.github.hotm.mod.block.AuraNodeBlock
import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.world.aura.Aura
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
import com.kneelawk.graphlib.api.util.NodePos

class AurameterItem(settings: Settings) : Item(settings), InteractionCanceler {
    override fun shouldCancelInteraction(usageContext: ItemUsageContext): Boolean {
        return true
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val user = context.player ?: return ActionResult.FAIL

        val world = context.world
        if (user.isSneaking) {
            return if (world.isClient) {
                ActionResult.SUCCESS
            } else {
                printChunkAura(world as ServerWorld, user)
                ActionResult.CONSUME
            }
        }

        val pos = context.blockPos
        val block = context.world.getBlockState(pos).block as? AuraNodeBlock ?: return ActionResult.FAIL
        val blockNode = block.getSelectedBlockNode(context)
        val nodePos = NodePos(pos, blockNode)
        val view = HotMUniverses.NETWORKS.getSidedGraphView(world) ?: return ActionResult.FAIL

        val holder = view.getNodeAt(nodePos) ?: return ActionResult.FAIL
        val node = holder.getNodeEntity(ValuedAuraNode::class.java) ?: return ActionResult.FAIL

        return if (world.isClient) {
            ActionResult.SUCCESS
        } else {
            user.sendMessage(msg("aurameter.node", node.value), true)
            ActionResult.CONSUME
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
        val sectionPos = ChunkSectionPos.from(player.blockPos)
        val base = Aura.getBase(world, sectionPos)
        val total = Aura.get(world, sectionPos)

        player.sendMessage(msg("aurameter.chunk", base, total), true)
    }
}
