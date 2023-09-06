package com.github.hotm.mod.item

import com.github.hotm.mod.Constants.msg
import com.github.hotm.mod.Constants.str
import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.auranet.AuraNodeUtils
import com.github.hotm.mod.auranet.ChildAuraNode
import com.github.hotm.mod.auranet.ParentAuraNode
import com.github.hotm.mod.block.AuraNodeBlock
import com.github.hotm.mod.node.HotMUniverses
import com.github.hotm.mod.node.aura.AuraLinkKey
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import com.kneelawk.graphlib.api.util.LinkPos
import com.kneelawk.graphlib.api.util.NodePos

class NodeTunerItem(settings: Settings) : Item(settings) {
    companion object {
        private val KEY = str("tuner_node_pos")

        fun hasPosition(stack: ItemStack): Boolean {
            return stack.getSubNbt(KEY) != null
        }

        fun setPosition(stack: ItemStack, pos: NodePos) {
            stack.setSubNbt(KEY, pos.toNbt())
        }

        fun getPosition(stack: ItemStack): NodePos? {
            return stack.getSubNbt(KEY)?.let { NodePos.fromNbt(it, HotMUniverses.NETWORKS) }
        }

        fun removePosition(stack: ItemStack) {
            stack.removeSubNbt(KEY)
        }
    }

//    override fun shouldCancelInteraction(usageContext: ItemUsageContext): Boolean {
//        return true
//    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return hasPosition(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val pos = context.blockPos
        val stack = context.stack
        val state = world.getBlockState(pos)
        val block1 = state.block
        val block = if (block1 is AuraNodeBlock) block1 else {
            HotMLog.LOG.info("$block1 is not an AuraNodeBlock")
            return ActionResult.FAIL
        }
        val node = block.getSelectedBlockNode(context)
        val nodePos = NodePos(pos, node)
        val player = context.player

        if (world.isClient || world !is ServerWorld) return ActionResult.CONSUME

        val existingPos = getPosition(stack)

        if (existingPos != null) {
            // don't allow nodes to connect to themselves, instead cancel the connection
            if (existingPos == nodePos) {
                removePosition(stack)

                return ActionResult.SUCCESS
            }

            // connect the two nodes
            val graphWorld = HotMUniverses.NETWORKS.getServerGraphWorld(world)

            val parentHolder = graphWorld.getNodeAt(existingPos) ?: return ActionResult.FAIL
            val childHolder = graphWorld.getNodeAt(nodePos) ?: return ActionResult.FAIL

            val parent = parentHolder.getNodeEntity(ParentAuraNode::class.java) ?: return ActionResult.FAIL
            val child = childHolder.getNodeEntity(ChildAuraNode::class.java) ?: return ActionResult.FAIL

            if (graphWorld.linkExistsAt(LinkPos(parentHolder.pos, childHolder.pos, AuraLinkKey))) {
                AuraNodeUtils.disconnect(graphWorld, parent, child)
                player?.sendMessage(msg("node_tuner.disconnected"), true)

                removePosition(stack)

                return ActionResult.SUCCESS
            } else {
                val res = AuraNodeUtils.connect(graphWorld, parent, child)
                player?.sendMessage(res.msg, true)

                removePosition(stack)

                return if (res.successful) ActionResult.SUCCESS else ActionResult.FAIL
            }
        } else {
            // check that the node we're selecting is actually a parent aura node
            val graphWorld = HotMUniverses.NETWORKS.getServerGraphWorld(world)
            val parentHolder = graphWorld.getNodeAt(nodePos) ?: return ActionResult.FAIL
            if (parentHolder.getNodeEntity() !is ParentAuraNode) return ActionResult.FAIL

            setPosition(stack, nodePos)

            return ActionResult.SUCCESS
        }
    }
}
